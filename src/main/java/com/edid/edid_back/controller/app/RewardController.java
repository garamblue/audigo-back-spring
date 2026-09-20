package com.audigo.audigo_back.controller.app;

import com.audigo.audigo_back.dto.response.reward.RewardBalanceResponseDto;
import com.audigo.audigo_back.dto.response.reward.RewardExpirationResponseDto;
import com.audigo.audigo_back.dto.response.reward.RewardHistoryResponseDto;
import com.audigo.audigo_back.service.reward.RewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Tag(name = "Member Reward", description = "회원 리워드 API")
@RestController
@RequestMapping("/api/mbr/balance")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    @Operation(summary = "현재 리워드 잔액 조회", description = "회원의 현재 리워드 포인트 잔액을 조회합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    public ResponseEntity<RewardBalanceResponseDto> getBalance(
            @AuthenticationPrincipal String email,
            @RequestParam BigInteger mIdx) {

        RewardBalanceResponseDto response = rewardService.getBalance(mIdx);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "리워드 적립/사용 내역 조회", description = "회원의 리워드 거래 내역을 조회합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/reward-history")
    public ResponseEntity<RewardHistoryResponseDto> getRewardHistory(
            @AuthenticationPrincipal String email,
            @RequestParam BigInteger mIdx,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        if (startDate == null) {
            startDate = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        }
        if (endDate == null) {
            endDate = LocalDateTime.now();
        }

        Pageable pageable = PageRequest.of(page, size);
        RewardHistoryResponseDto response = rewardService.getRewardHistory(mIdx, startDate, endDate, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "만료 예정 리워드 조회", description = "1개월 후 만료 예정인 리워드 금액을 조회합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/scheduled-expire")
    public ResponseEntity<RewardExpirationResponseDto> getScheduledExpiration(
            @AuthenticationPrincipal String email,
            @RequestParam BigInteger mIdx) {

        RewardExpirationResponseDto response = rewardService.getScheduledExpiration(mIdx);
        return ResponseEntity.ok(response);
    }
}
