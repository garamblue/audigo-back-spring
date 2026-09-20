package com.audigo.audigo_back.controller.app;

import com.audigo.audigo_back.dto.response.web3.TokenBalanceResponse;
import com.audigo.audigo_back.dto.response.web3.TransactionResponse;
import com.audigo.audigo_back.dto.response.web3.WalletResponse;
import com.audigo.audigo_back.service.web3.Web3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

/**
 * Web3/블록체인 관련 API
 */
@Tag(name = "Web3 API", description = "블록체인 지갑 및 토큰 관리 API")
@RestController
@RequestMapping("/api/mbr/web3")
@RequiredArgsConstructor
@Slf4j
public class Web3Controller {

    private final Web3Service web3Service;

    /**
     * 지갑 등록 (회원가입 시 10 토큰 지급)
     */
    @Operation(summary = "지갑 등록", description = "사용자 지갑 주소를 등록하고 환영 토큰 10개를 지급합니다")
    @PostMapping("/wallet/register")
    public ResponseEntity<WalletResponse> registerWallet(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request
    ) {
        try {
            BigInteger mIdx = new BigInteger(userDetails.getUsername());
            String walletAddress = request.get("addr");

            if (walletAddress == null || walletAddress.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            WalletResponse response = web3Service.registerWallet(mIdx, walletAddress);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid wallet address", e);
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            log.error("Wallet already registered", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Failed to register wallet", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 지갑 주소 변경
     */
    @Operation(summary = "지갑 주소 변경", description = "등록된 지갑 주소를 새로운 주소로 변경합니다")
    @PutMapping("/wallet/change")
    public ResponseEntity<WalletResponse> changeWallet(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request
    ) {
        try {
            BigInteger mIdx = new BigInteger(userDetails.getUsername());
            String newWalletAddress = request.get("addr");

            if (newWalletAddress == null || newWalletAddress.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }

            WalletResponse response = web3Service.changeWallet(mIdx, newWalletAddress);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid wallet address", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Failed to change wallet", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 토큰 잔액 조회
     */
    @Operation(summary = "토큰 잔액 조회", description = "블록체인에서 실시간 토큰 및 BNB 잔액을 조회합니다")
    @GetMapping("/balance")
    public ResponseEntity<TokenBalanceResponse> getBalance(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            BigInteger mIdx = new BigInteger(userDetails.getUsername());
            TokenBalanceResponse response = web3Service.getTokenBalance(mIdx);
            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            log.error("Wallet not registered", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Failed to get balance", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 보상금 → 토큰 스왑
     */
    @Operation(summary = "보상금 스왑", description = "보상금을 토큰으로 교환합니다 (1000~10000, 100 단위)")
    @PostMapping("/swap")
    public ResponseEntity<TransactionResponse> swapRewardToToken(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> request
    ) {
        try {
            BigInteger mIdx = new BigInteger(userDetails.getUsername());
            BigDecimal rewardAmount = new BigDecimal(request.get("rwds").toString());

            TransactionResponse response = web3Service.swapRewardToToken(mIdx, rewardAmount);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid swap amount", e);
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            log.error("Insufficient balance or wallet not registered", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Failed to swap", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 스왑 수수료 조회
     */
    @Operation(summary = "스왑 수수료 조회", description = "현재 적용 중인 스왑 수수료를 조회합니다")
    @GetMapping("/swap/fee")
    public ResponseEntity<Map<String, Object>> getSwapFee(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            BigDecimal fee = web3Service.getSwapFee();
            Map<String, Object> response = Map.of(
                    "fee", fee,
                    "exchange", "0.01",  // 임시 교환비 (100 보상금 = 1 토큰)
                    "remain", 10000      // 일일 스왑 한도 (추후 구현)
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to get swap fee", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 트랜잭션 상태 조회
     */
    @Operation(summary = "트랜잭션 상태 조회", description = "트랜잭션 해시로 블록체인 트랜잭션 상태를 조회합니다")
    @GetMapping("/transaction/{txHash}")
    public ResponseEntity<TransactionResponse> getTransactionStatus(
            @PathVariable String txHash
    ) {
        try {
            TransactionResponse response = web3Service.getTransactionStatus(txHash);
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            log.error("Invalid transaction hash", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Failed to get transaction status", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
