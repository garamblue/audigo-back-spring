package com.audigo.audigo_back.controller.app;

import com.audigo.audigo_back.dto.response.advertisement.AdsAudioResponse;
import com.audigo.audigo_back.dto.response.advertisement.AdsResponseResult;
import com.audigo.audigo_back.service.advertisement.AdvertisementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * 광고 API
 */
@Tag(name = "Advertisement API", description = "광고 배포 및 응답 API")
@RestController
@RequestMapping("/api/mbr/ads")
@RequiredArgsConstructor
@Slf4j
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    /**
     * 광고 배포 (사용자에게 광고 제공)
     */
    @Operation(summary = "광고 배포", description = "사용자에게 시청 가능한 광고 목록을 제공합니다 (일일 30개 제한)")
    @PostMapping("/send")
    public ResponseEntity<List<AdsAudioResponse>> sendAds(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request
    ) {
        try {
            BigInteger mIdx = new BigInteger(userDetails.getUsername());
            String timezone = request.getOrDefault("timezone", "Asia/Seoul");

            List<AdsAudioResponse> ads = advertisementService.distributeAds(mIdx, timezone);
            return ResponseEntity.ok(ads);

        } catch (Exception e) {
            log.error("Failed to send ads", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 광고 응답 처리 (시청 완료 및 퀴즈 응답)
     */
    @Operation(summary = "광고 응답", description = "광고 시청 완료 및 퀴즈 정답을 제출하여 보상을 받습니다")
    @PostMapping("/response")
    public ResponseEntity<AdsResponseResult> submitResponse(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> request
    ) {
        try {
            BigInteger mIdx = new BigInteger(userDetails.getUsername());
            Long ashIdx = Long.valueOf(request.get("ash_idx").toString());
            String answer = request.get("answer") != null ? request.get("answer").toString() : null;

            AdsResponseResult result = advertisementService.submitAdResponse(mIdx, ashIdx, answer);
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException e) {
            log.error("Invalid request", e);
            return ResponseEntity.badRequest().build();
        } catch (IllegalStateException e) {
            log.error("Already responded", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Failed to submit response", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 남은 광고 시청 가능 횟수 조회
     */
    @Operation(summary = "남은 광고 횟수 조회", description = "오늘 시청 가능한 남은 광고 횟수를 조회합니다")
    @GetMapping("/remaining")
    public ResponseEntity<Map<String, Object>> getRemainingCount(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "Asia/Seoul") String timezone
    ) {
        try {
            BigInteger mIdx = new BigInteger(userDetails.getUsername());
            int remaining = advertisementService.getRemainingAdCount(mIdx, timezone);

            Map<String, Object> response = Map.of(
                    "remaining", remaining,
                    "limit", 30,
                    "timezone", timezone
            );
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to get remaining count", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 광고 상세 조회
     */
    @Operation(summary = "광고 상세 조회", description = "특정 광고의 상세 정보를 조회합니다")
    @GetMapping("/{aaIdx}")
    public ResponseEntity<AdsAudioResponse> getAdDetail(
            @PathVariable Long aaIdx
    ) {
        try {
            AdsAudioResponse ad = advertisementService.getAdDetail(aaIdx);
            return ResponseEntity.ok(ad);

        } catch (IllegalArgumentException e) {
            log.error("Ad not found", e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Failed to get ad detail", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 광고 시청 이력 조회
     */
    @Operation(summary = "광고 시청 이력", description = "사용자의 광고 시청 이력을 조회합니다")
    @GetMapping("/history")
    public ResponseEntity<List<AdsResponseResult>> getAdHistory(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        try {
            BigInteger mIdx = new BigInteger(userDetails.getUsername());
            List<AdsResponseResult> history = advertisementService.getAdHistory(mIdx);
            return ResponseEntity.ok(history);

        } catch (Exception e) {
            log.error("Failed to get ad history", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
