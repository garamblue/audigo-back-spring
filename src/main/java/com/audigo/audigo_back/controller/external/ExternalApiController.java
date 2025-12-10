package com.audigo.audigo_back.controller.external;

import com.audigo.audigo_back.dto.request.external.PincruxRewardRequest;
import com.audigo.audigo_back.dto.response.external.LinkPriceBannerResponse;
import com.audigo.audigo_back.service.external.ExternalApiService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 외부 API 연동 (Pincrux, LinkPrice)
 */
@Tag(name = "External API", description = "외부 API 연동 - Pincrux 오퍼월, LinkPrice 배너")
@RestController
@RequestMapping("/api/external")
@RequiredArgsConstructor
@Slf4j
public class ExternalApiController {

    private final ExternalApiService externalApiService;

    // ========== Pincrux ==========

    /**
     * Pincrux 오퍼월 보상 처리 (Callback)
     */
    @Operation(summary = "Pincrux 보상 처리", description = "Pincrux 오퍼월 광고 완료 시 보상을 처리합니다 (Callback)")
    @PostMapping("/pincrux/reward")
    public ResponseEntity<Map<String, String>> processPincruxReward(
            @RequestBody PincruxRewardRequest request
    ) {
        try {
            externalApiService.processPincruxReward(request);

            Map<String, String> response = new HashMap<>();
            response.put("code", "00");  // 성공
            return ResponseEntity.status(201).body(response);

        } catch (IllegalStateException e) {
            log.error("Duplicate Pincrux transaction", e);
            Map<String, String> response = new HashMap<>();
            response.put("code", "01");  // 중복
            return ResponseEntity.badRequest().body(response);
        } catch (IllegalArgumentException e) {
            log.error("Invalid Pincrux request", e);
            Map<String, String> response = new HashMap<>();
            response.put("code", "05");  // 회원 없음
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            log.error("Failed to process Pincrux reward", e);
            Map<String, String> response = new HashMap<>();
            response.put("code", "99");  // 오류
            return ResponseEntity.internalServerError().body(response);
        }
    }

    // ========== LinkPrice ==========

    /**
     * LinkPrice 랜덤 배너 3개 조회
     */
    @Operation(summary = "LinkPrice 배너 조회", description = "랜덤 배너 3개를 조회합니다")
    @GetMapping("/linkprice/banners")
    public ResponseEntity<List<LinkPriceBannerResponse>> getRandomBanners() {
        try {
            List<LinkPriceBannerResponse> banners = externalApiService.getRandomBanners();
            return ResponseEntity.ok(banners);

        } catch (Exception e) {
            log.error("Failed to get LinkPrice banners", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * LinkPrice 배너 클릭 보상
     */
    @Operation(summary = "LinkPrice 배너 클릭", description = "배너 클릭 시 보상을 받습니다 (1일 1회)")
    @PostMapping("/linkprice/click")
    public ResponseEntity<Map<String, Object>> clickLinkPriceBanner(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> request
    ) {
        try {
            BigInteger mIdx = new BigInteger(userDetails.getUsername());
            Long lbIdx = Long.valueOf(request.get("lb_idx").toString());
            String timezone = request.getOrDefault("timezone", "Asia/Seoul").toString();

            BigDecimal rewardAmount = externalApiService.clickLinkPriceBanner(mIdx, lbIdx, timezone);

            Map<String, Object> response = new HashMap<>();
            response.put("r_amt", rewardAmount);
            return ResponseEntity.status(201).body(response);

        } catch (IllegalStateException e) {
            log.error("Already clicked banner today", e);
            return ResponseEntity.badRequest().build();
        } catch (IllegalArgumentException e) {
            log.error("Invalid banner request", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Failed to click LinkPrice banner", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
