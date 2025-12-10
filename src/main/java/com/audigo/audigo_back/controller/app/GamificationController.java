package com.audigo.audigo_back.controller.app;

import com.audigo.audigo_back.dto.response.gamification.*;
import com.audigo.audigo_back.service.gamification.GamificationService;
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
 * 게임화 기능 API (출석, 룰렛, 운세)
 */
@Tag(name = "Gamification API", description = "게임화 기능 - 출석체크, 룰렛, 운세")
@RestController
@RequestMapping("/api/mbr/fun")
@RequiredArgsConstructor
@Slf4j
public class GamificationController {

    private final GamificationService gamificationService;

    // ========== 출석 체크 ==========

    /**
     * 오늘의 출석 현황 조회
     */
    @Operation(summary = "출석 현황 조회", description = "오늘의 출석 현황과 연속 출석 일수를 조회합니다")
    @GetMapping("/attendance/today")
    public ResponseEntity<AttendanceResponse> getAttendanceToday(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "Asia/Seoul") String timezone
    ) {
        try {
            BigInteger mIdx = new BigInteger(userDetails.getUsername());
            AttendanceResponse response = gamificationService.getAttendanceToday(mIdx, timezone);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to get attendance today", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 출석 체크 실행
     */
    @Operation(summary = "출석 체크", description = "출석 체크를 실행하고 보상을 받습니다 (7일 연속 시 추가 보상)")
    @PostMapping("/attendance")
    public ResponseEntity<AttendanceResponse> checkAttendance(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request
    ) {
        try {
            BigInteger mIdx = new BigInteger(userDetails.getUsername());
            String timezone = request.getOrDefault("timezone", "Asia/Seoul");

            AttendanceResponse response = gamificationService.checkAttendance(mIdx, timezone);
            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            log.error("Already checked in", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Failed to check attendance", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========== 룰렛 ==========

    /**
     * 룰렛 상태 조회
     */
    @Operation(summary = "룰렛 상태 조회", description = "보유 쿠폰 수, 룰렛 정책, 최근 당첨자 목록을 조회합니다")
    @GetMapping("/roulette/status")
    public ResponseEntity<RouletteStatusResponse> getRouletteStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "KO") String lang,
            @RequestParam(defaultValue = "Asia/Seoul") String timezone
    ) {
        try {
            BigInteger mIdx = new BigInteger(userDetails.getUsername());
            RouletteStatusResponse response = gamificationService.getRouletteStatus(mIdx, lang, timezone);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to get roulette status", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 룰렛 실행
     */
    @Operation(summary = "룰렛 실행", description = "룰렛을 돌려 랜덤 보상을 받습니다 (쿠폰 1개 소모)")
    @PostMapping("/roulette/play")
    public ResponseEntity<RoulettePlayResponse> playRoulette(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request
    ) {
        try {
            BigInteger mIdx = new BigInteger(userDetails.getUsername());
            String lang = request.getOrDefault("lang", "KO");

            RoulettePlayResponse response = gamificationService.playRoulette(mIdx, lang);
            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            log.error("No roulette coupon available", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Failed to play roulette", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 룰렛 쿠폰 추가
     */
    @Operation(summary = "룰렛 쿠폰 받기", description = "룰렛 쿠폰을 받습니다 (3시간마다 1회)")
    @PostMapping("/roulette/coupon")
    public ResponseEntity<Void> addRouletteCoupon(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request
    ) {
        try {
            BigInteger mIdx = new BigInteger(userDetails.getUsername());
            String timezone = request.getOrDefault("timezone", "Asia/Seoul");

            gamificationService.addRouletteCoupon(mIdx, timezone);
            return ResponseEntity.ok().build();

        } catch (IllegalStateException e) {
            log.error("Already received coupon", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Failed to add roulette coupon", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    // ========== 운세 ==========

    /**
     * 오늘의 운세 조회
     */
    @Operation(summary = "오늘의 운세", description = "오늘의 서양 별자리 운세와 동양 띠 운세를 조회합니다")
    @GetMapping("/horoscope/daily")
    public ResponseEntity<HoroscopeResponse> getDailyHoroscope(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "KO") String lang,
            @RequestParam(defaultValue = "Asia/Seoul") String timezone
    ) {
        try {
            BigInteger mIdx = new BigInteger(userDetails.getUsername());
            HoroscopeResponse response = gamificationService.getDailyHoroscope(mIdx, lang, timezone);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Failed to get daily horoscope", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 운세 보상 받기
     */
    @Operation(summary = "운세 보상 받기", description = "오늘의 운세를 확인하고 보상을 받습니다 (1일 1회)")
    @PostMapping("/horoscope/reward")
    public ResponseEntity<Map<String, Object>> claimHoroscopeReward(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> request
    ) {
        try {
            BigInteger mIdx = new BigInteger(userDetails.getUsername());
            String timezone = request.getOrDefault("timezone", "Asia/Seoul");

            BigDecimal amount = gamificationService.claimHoroscopeReward(mIdx, timezone);
            Map<String, Object> response = Map.of("r_amt", amount);
            return ResponseEntity.ok(response);

        } catch (IllegalStateException e) {
            log.error("Already claimed horoscope reward", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Failed to claim horoscope reward", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}
