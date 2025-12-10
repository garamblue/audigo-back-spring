package com.audigo.audigo_back.service.gamification;

import com.audigo.audigo_back.dto.response.gamification.*;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * 게임화 서비스 인터페이스
 */
public interface GamificationService {

    // ========== 출석 체크 ==========

    /**
     * 오늘의 출석 현황 조회
     */
    AttendanceResponse getAttendanceToday(BigInteger mIdx, String timezone);

    /**
     * 출석 체크 실행
     */
    AttendanceResponse checkAttendance(BigInteger mIdx, String timezone);

    // ========== 룰렛 ==========

    /**
     * 룰렛 상태 조회
     */
    RouletteStatusResponse getRouletteStatus(BigInteger mIdx, String lang, String timezone);

    /**
     * 룰렛 실행
     */
    RoulettePlayResponse playRoulette(BigInteger mIdx, String lang);

    /**
     * 룰렛 쿠폰 추가
     */
    void addRouletteCoupon(BigInteger mIdx, String timezone);

    // ========== 운세 ==========

    /**
     * 오늘의 운세 조회
     */
    HoroscopeResponse getDailyHoroscope(BigInteger mIdx, String lang, String timezone);

    /**
     * 운세 보상 받기
     */
    BigDecimal claimHoroscopeReward(BigInteger mIdx, String timezone);
}
