package com.audigo.audigo_back.controller.app;

import com.audigo.audigo_back.dto.request.market.ExchangeRequestDto;
import com.audigo.audigo_back.dto.response.market.ProductListResponseDto;
import com.audigo.audigo_back.service.market.MarketplaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.Map;

@Tag(name = "Marketplace", description = "상품권 교환 마켓플레이스 API")
@RestController
@RequestMapping("/api/mbr/market")
@RequiredArgsConstructor
public class MarketplaceController {

    private final MarketplaceService marketplaceService;

    @Operation(summary = "상품 목록 조회", description = "교환 가능한 상품권 목록을 조회합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/goods/get-list")
    public ResponseEntity<ProductListResponseDto> getProductList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        ProductListResponseDto response = marketplaceService.getProductList(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "상품권 교환", description = "리워드 포인트로 상품권을 교환합니다.")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/exchange")
    public ResponseEntity<Map<String, String>> exchangeGift(
            @AuthenticationPrincipal String email,
            @RequestParam BigInteger mIdx,
            @Valid @RequestBody ExchangeRequestDto request) {

        String trId = marketplaceService.exchangeGift(mIdx, request);
        return ResponseEntity.ok(Map.of("message", "Exchange successful", "trId", trId));
    }

    @Operation(summary = "쿠폰 재발송", description = "이미 교환한 쿠폰을 재발송합니다 (최대 3회)")
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/resend")
    public ResponseEntity<Map<String, String>> resendCoupon(
            @AuthenticationPrincipal String email,
            @RequestParam BigInteger mIdx,
            @RequestParam BigInteger gehIdx) {

        marketplaceService.resendCoupon(mIdx, gehIdx);
        return ResponseEntity.ok(Map.of("message", "Coupon resent successfully"));
    }
}
