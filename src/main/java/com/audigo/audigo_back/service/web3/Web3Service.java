package com.audigo.audigo_back.service.web3;

import com.audigo.audigo_back.dto.response.web3.TokenBalanceResponse;
import com.audigo.audigo_back.dto.response.web3.TransactionResponse;
import com.audigo.audigo_back.dto.response.web3.WalletResponse;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Web3 서비스 인터페이스
 */
public interface Web3Service {

    /**
     * 지갑 등록 (회원가입 시 10 토큰 지급)
     */
    WalletResponse registerWallet(BigInteger mIdx, String walletAddress);

    /**
     * 지갑 주소 변경
     */
    WalletResponse changeWallet(BigInteger mIdx, String newWalletAddress);

    /**
     * 토큰 잔액 조회
     */
    TokenBalanceResponse getTokenBalance(BigInteger mIdx);

    /**
     * 보상금을 토큰으로 스왑
     */
    TransactionResponse swapRewardToToken(BigInteger mIdx, BigDecimal rewardAmount);

    /**
     * 스왑 수수료 조회
     */
    BigDecimal getSwapFee();

    /**
     * 트랜잭션 상태 조회
     */
    TransactionResponse getTransactionStatus(String txHash);

    /**
     * 토큰 전송 (내부 - 서버에서 사용자에게)
     */
    TransactionResponse transferToken(String toAddress, BigDecimal amount, String code);
}
