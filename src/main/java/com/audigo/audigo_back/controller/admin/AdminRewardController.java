package com.audigo.audigo_back.controller.admin;

import com.audigo.audigo_back.dto.request.reward.RewardAdjustRequestDto;
import com.audigo.audigo_back.service.reward.RewardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Map;

@Tag(name = "Admin Reward", description = "관리자 리워드 관리 API")
@RestController
@RequestMapping("/api/adm/rwds")
@RequiredArgsConstructor
public class AdminRewardController {

    private final RewardService rewardService;

    @Operation(summary = "리워드 조정 생성", description = "회원의 리워드를 수동으로 조정합니다 (즉시 또는 예약)")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/adjust/post-adjust")
    public ResponseEntity<Map<String, String>> createAdjustment(
            @AuthenticationPrincipal String adminId,
            @Valid @RequestBody RewardAdjustRequestDto request) {

        // TODO: Extract admin ID from authentication
        BigInteger adminIdx = BigInteger.ONE; // Placeholder

        rewardService.createAdjustment(request, adminIdx);

        return ResponseEntity.ok(Map.of("message", "Adjustment created successfully"));
    }

    @Operation(summary = "예약된 리워드 조정 처리", description = "스케줄러용: 예약된 리워드 조정을 실행합니다")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/adjust/process-scheduled")
    public ResponseEntity<Map<String, String>> processScheduledAdjustments() {
        rewardService.processScheduledAdjustments();
        return ResponseEntity.ok(Map.of("message", "Scheduled adjustments processed"));
    }

    @Operation(summary = "리워드 만료 처리", description = "스케줄러용: 만료된 리워드를 처리합니다")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/expiration/process")
    public ResponseEntity<Map<String, String>> processRewardExpiration() {
        rewardService.processRewardExpiration();
        return ResponseEntity.ok(Map.of("message", "Reward expiration processed"));
    }
}
