package com.audigo.audigo_back.service.web3.impl;

import com.audigo.audigo_back.config.Web3Config;
import com.audigo.audigo_back.dto.response.web3.TokenBalanceResponse;
import com.audigo.audigo_back.dto.response.web3.TransactionResponse;
import com.audigo.audigo_back.dto.response.web3.WalletResponse;
import com.audigo.audigo_back.entity.reward.RewardBalanceEntity;
import com.audigo.audigo_back.entity.reward.RewardExchangeEntity;
import com.audigo.audigo_back.entity.web3.EWalletEntity;
import com.audigo.audigo_back.entity.web3.InnTxHisEntity;
import com.audigo.audigo_back.entity.web3.SwapFeeEntity;
import com.audigo.audigo_back.repository.reward.RewardBalanceRepository;
import com.audigo.audigo_back.repository.reward.RewardExchangeRepository;
import com.audigo.audigo_back.repository.web3.EWalletRepository;
import com.audigo.audigo_back.repository.web3.InnTxHisRepository;
import com.audigo.audigo_back.repository.web3.SwapFeeRepository;
import com.audigo.audigo_back.service.web3.Web3Service;
import com.audigo.audigo_back.util.Web3Util;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.RawTransaction;
import org.web3j.crypto.TransactionEncoder;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthGetTransactionCount;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Collections;

/**
 * Web3 서비스 구현
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class Web3ServiceImpl implements Web3Service {

    private final Web3j web3j;
    private final Web3Config web3Config;
    private final EWalletRepository eWalletRepository;
    private final InnTxHisRepository innTxHisRepository;
    private final SwapFeeRepository swapFeeRepository;
    private final RewardBalanceRepository rewardBalanceRepository;
    private final RewardExchangeRepository rewardExchangeRepository;

    // TODO: KMS Credentials 구현 필요
    // private final Credentials serverCredentials;

    /**
     * 지갑 등록 (회원가입 시 10 토큰 지급)
     */
    @Override
    @Transactional
    public WalletResponse registerWallet(BigInteger mIdx, String walletAddress) {
        log.info("Register wallet for member: {}, address: {}", mIdx, walletAddress);

        // 주소 유효성 검증
        if (!Web3Util.isValidAddress(walletAddress)) {
            throw new IllegalArgumentException("Invalid wallet address");
        }

        // 기존 지갑 조회
        EWalletEntity wallet = eWalletRepository.findByMIdx(mIdx)
                .orElseThrow(() -> new RuntimeException("Wallet not found for member: " + mIdx));

        // 이미 등록된 주소가 있으면 에러
        if (wallet.getAddr() != null && !wallet.getAddr().isEmpty()) {
            throw new IllegalStateException("Wallet already registered");
        }

        // 지갑 주소 업데이트
        wallet.setAddr(walletAddress);
        eWalletRepository.save(wallet);

        // 회원가입 축하 토큰 10개 지급
        BigDecimal welcomeTokens = new BigDecimal("10");
        TransactionResponse txResponse = transferToken(walletAddress, welcomeTokens, "T001001"); // signup code

        // 트랜잭션 기록 저장
        InnTxHisEntity txHistory = new InnTxHisEntity();
        txHistory.setTxHash(txResponse.getTxHash());
        txHistory.setEwIdx(wallet.getEwIdx());
        txHistory.setFromAddr(web3Config.getMain().getWallet().getAddress());
        txHistory.setAmt(welcomeTokens);
        txHistory.setStatus("pending");
        txHistory.setCd("T001001");  // signup
        innTxHisRepository.save(txHistory);

        return WalletResponse.builder()
                .ewIdx(wallet.getEwIdx())
                .address(walletAddress)
                .tokenBalance(welcomeTokens)
                .txHash(txResponse.getTxHash())
                .build();
    }

    /**
     * 지갑 주소 변경
     */
    @Override
    @Transactional
    public WalletResponse changeWallet(BigInteger mIdx, String newWalletAddress) {
        log.info("Change wallet for member: {}, new address: {}", mIdx, newWalletAddress);

        // 주소 유효성 검증
        if (!Web3Util.isValidAddress(newWalletAddress)) {
            throw new IllegalArgumentException("Invalid wallet address");
        }

        // 기존 지갑 조회
        EWalletEntity wallet = eWalletRepository.findByMIdx(mIdx)
                .orElseThrow(() -> new RuntimeException("Wallet not found for member: " + mIdx));

        // 지갑 주소 변경 (잔액은 0으로 초기화)
        wallet.setAddr(newWalletAddress);
        wallet.setTokenAmt(BigDecimal.ZERO);
        wallet.setBnbAmt(BigDecimal.ZERO);
        eWalletRepository.save(wallet);

        return WalletResponse.builder()
                .ewIdx(wallet.getEwIdx())
                .address(newWalletAddress)
                .tokenBalance(BigDecimal.ZERO)
                .bnbBalance(BigDecimal.ZERO)
                .build();
    }

    /**
     * 토큰 잔액 조회
     */
    @Override
    public TokenBalanceResponse getTokenBalance(BigInteger mIdx) {
        try {
            EWalletEntity wallet = eWalletRepository.findByMIdx(mIdx)
                    .orElseThrow(() -> new RuntimeException("Wallet not found for member: " + mIdx));

            if (wallet.getAddr() == null || wallet.getAddr().isEmpty()) {
                throw new IllegalStateException("Wallet address not registered");
            }

            // 블록체인에서 실제 잔액 조회
            BigInteger tokenBalanceWei = Web3Util.getTokenBalance(
                    web3j,
                    web3Config.getToken().getAddress(),
                    wallet.getAddr()
            );

            BigInteger bnbBalanceWei = web3j.ethGetBalance(
                    wallet.getAddr(),
                    DefaultBlockParameterName.LATEST
            ).send().getBalance();

            BigDecimal tokenBalance = Web3Util.weiToGwei(tokenBalanceWei);
            BigDecimal bnbBalance = Web3Util.weiToEther(bnbBalanceWei);

            // DB 업데이트
            wallet.setTokenAmt(tokenBalance);
            wallet.setBnbAmt(bnbBalance);
            eWalletRepository.save(wallet);

            return TokenBalanceResponse.builder()
                    .address(wallet.getAddr())
                    .tokenBalance(tokenBalance)
                    .bnbBalance(bnbBalance)
                    .build();

        } catch (Exception e) {
            log.error("Failed to get token balance for member: {}", mIdx, e);
            throw new RuntimeException("Failed to get token balance", e);
        }
    }

    /**
     * 보상금을 토큰으로 스왑
     */
    @Override
    @Transactional
    public TransactionResponse swapRewardToToken(BigInteger mIdx, BigDecimal rewardAmount) {
        log.info("Swap reward to token for member: {}, amount: {}", mIdx, rewardAmount);

        // 스왑 금액 검증 (1000~10000, 100 단위)
        if (rewardAmount.compareTo(new BigDecimal("1000")) < 0 ||
                rewardAmount.compareTo(new BigDecimal("10000")) > 0 ||
                rewardAmount.remainder(new BigDecimal("100")).compareTo(BigDecimal.ZERO) != 0) {
            throw new IllegalArgumentException("Invalid swap amount (must be 1000~10000, multiple of 100)");
        }

        // 지갑 조회
        EWalletEntity wallet = eWalletRepository.findByMIdx(mIdx)
                .orElseThrow(() -> new RuntimeException("Wallet not found for member: " + mIdx));

        if (wallet.getAddr() == null || wallet.getAddr().isEmpty()) {
            throw new IllegalStateException("Wallet address not registered");
        }

        // 보상금 잔액 확인
        RewardBalanceEntity rewardBalance = rewardBalanceRepository.findByMIdx(mIdx)
                .orElseThrow(() -> new RuntimeException("Reward balance not found"));

        if (rewardBalance.getSumAmt().compareTo(rewardAmount) < 0) {
            throw new IllegalStateException("Insufficient reward balance");
        }

        // 스왑 수수료 조회
        BigDecimal feeRatio = getSwapFee();

        // 토큰 수량 계산 (임시: 100 보상금 = 1 토큰)
        BigDecimal tokenAmount = rewardAmount.divide(new BigDecimal("100"));

        // 수수료 차감
        BigDecimal feeAmount = tokenAmount.multiply(feeRatio).divide(new BigDecimal("100"));
        BigDecimal finalTokenAmount = tokenAmount.subtract(feeAmount);

        // 토큰 전송
        TransactionResponse txResponse = transferToken(wallet.getAddr(), finalTokenAmount, "T001002"); // swap code

        // 보상금 차감
        rewardBalance.setSumAmt(rewardBalance.getSumAmt().subtract(rewardAmount));
        rewardBalanceRepository.save(rewardBalance);

        // 교환 내역 저장
        RewardExchangeEntity exchangeHistory = new RewardExchangeEntity();
        exchangeHistory.setMIdx(mIdx);
        exchangeHistory.setRAmt(rewardAmount);
        exchangeHistory.setCd("R0000"); // SWAP
        exchangeHistory.setTableNm("C001013"); // INN_TX_HIS code
        rewardExchangeRepository.save(exchangeHistory);

        // 트랜잭션 기록 저장
        InnTxHisEntity txHistory = new InnTxHisEntity();
        txHistory.setTxHash(txResponse.getTxHash());
        txHistory.setEwIdx(wallet.getEwIdx());
        txHistory.setFromAddr(web3Config.getMain().getWallet().getAddress());
        txHistory.setAmt(finalTokenAmount);
        txHistory.setStatus("pending");
        txHistory.setCd("T001002");  // swap
        innTxHisRepository.save(txHistory);

        return txResponse;
    }

    /**
     * 스왑 수수료 조회
     */
    @Override
    public BigDecimal getSwapFee() {
        SwapFeeEntity latestFee = swapFeeRepository.findLatest()
                .orElse(new SwapFeeEntity(null, new BigDecimal("5.00"), null)); // 기본 5%
        return latestFee.getRatio();
    }

    /**
     * 트랜잭션 상태 조회
     */
    @Override
    public TransactionResponse getTransactionStatus(String txHash) {
        try {
            if (!Web3Util.isValidTxHash(txHash)) {
                throw new IllegalArgumentException("Invalid transaction hash");
            }

            org.web3j.protocol.core.methods.response.Transaction transaction =
                    web3j.ethGetTransactionByHash(txHash).send().getTransaction().orElse(null);

            if (transaction == null) {
                throw new RuntimeException("Transaction not found: " + txHash);
            }

            TransactionReceipt receipt = web3j.ethGetTransactionReceipt(txHash)
                    .send().getTransactionReceipt().orElse(null);

            String status = receipt == null ? "pending" :
                    Web3Util.isTransactionSuccess(receipt) ? "success" : "failed";

            BigInteger blockNumber = receipt != null ? receipt.getBlockNumber() : null;
            BigInteger gasUsed = receipt != null ? receipt.getGasUsed() : null;

            return TransactionResponse.builder()
                    .txHash(txHash)
                    .status(status)
                    .fromAddress(transaction.getFrom())
                    .toAddress(transaction.getTo())
                    .amount(Web3Util.weiToGwei(transaction.getValue()))
                    .gasUsed(gasUsed != null ? new BigDecimal(gasUsed) : null)
                    .blockNumber(blockNumber != null ? blockNumber.longValue() : null)
                    .build();

        } catch (Exception e) {
            log.error("Failed to get transaction status: {}", txHash, e);
            throw new RuntimeException("Failed to get transaction status", e);
        }
    }

    /**
     * 토큰 전송 (내부 - 서버에서 사용자에게)
     * TODO: AWS KMS Credentials를 사용한 서명 구현 필요
     */
    @Override
    public TransactionResponse transferToken(String toAddress, BigDecimal amount, String code) {
        try {
            log.info("Transfer token to: {}, amount: {}, code: {}", toAddress, amount, code);

            // TODO: KMS를 통한 실제 트랜잭션 서명 및 전송 구현
            // 현재는 임시 구현 (실제로는 KMS Signer를 사용해야 함)

            String dummyTxHash = "0x" + String.format("%064x", System.currentTimeMillis());

            return TransactionResponse.builder()
                    .txHash(dummyTxHash)
                    .status("pending")
                    .fromAddress(web3Config.getMain().getWallet().getAddress())
                    .toAddress(toAddress)
                    .amount(amount)
                    .timestamp(LocalDateTime.now())
                    .build();

        } catch (Exception e) {
            log.error("Failed to transfer token", e);
            throw new RuntimeException("Failed to transfer token", e);
        }
    }
}
