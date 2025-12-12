package com.audigo.audigo_back.service.gamification;

import com.audigo.audigo_back.dto.response.gamification.*;
import com.audigo.audigo_back.entity.gamification.*;
import com.audigo.audigo_back.entity.reward.RewardBalanceEntity;
import com.audigo.audigo_back.entity.reward.RewardExchangeEntity;
import com.audigo.audigo_back.repository.gamification.*;
import com.audigo.audigo_back.repository.reward.RewardBalanceRepository;
import com.audigo.audigo_back.repository.reward.RewardExchangeRepository;
import com.audigo.audigo_back.repository.reward.RewardPolicyRepository;
import com.audigo.audigo_back.repository.reward.RewardTopupRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * 게임화 서비스 구현
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GamificationServiceImpl implements GamificationService {

    private final AttendanceRepository attendanceRepository;
    private final RouletteCouponRepository rouletteCouponRepository;
    private final RouletteHisRepository rouletteHisRepository;
    private final HoroscopeHisRepository horoscopeHisRepository;
    private final HoroscopeDailyRepository horoscopeDailyRepository;
    private final RewardPolicyRepository rewardPolicyRepository;
    private final RewardBalanceRepository rewardBalanceRepository;
    private final RewardExchangeRepository rewardExchangeRepository;
    private final RewardTopupRepository rewardTopupRepository;

    private static final Random random = new Random();

    // ========== 출석 체크 ==========

    @Override
    @Transactional(readOnly = true)
    public AttendanceResponse getAttendanceToday(BigInteger mIdx, String timezone) {
        ZoneId zoneId = ZoneId.of(timezone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime startOfDay = now.toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime endOfDay = startOfDay.plusDays(1);

        LocalDateTime startTime = startOfDay.toLocalDateTime();
        LocalDateTime endTime = endOfDay.toLocalDateTime();

        // 오늘 출석 여부 확인
        Long todayCount = attendanceRepository.countByMIdxAndDateRange(mIdx, startTime, endTime);
        boolean isCompleted = todayCount > 0;

        // 연속 출석 일수 계산
        int streak = calculateAttendanceStreak(mIdx, timezone);

        return new AttendanceResponse(streak, isCompleted);
    }

    @Override
    @Transactional
    public AttendanceResponse checkAttendance(BigInteger mIdx, String timezone) {
        ZoneId zoneId = ZoneId.of(timezone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime startOfDay = now.toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime endOfDay = startOfDay.plusDays(1);

        LocalDateTime startTime = startOfDay.toLocalDateTime();
        LocalDateTime endTime = endOfDay.toLocalDateTime();

        // 이미 출석했는지 확인
        Long todayCount = attendanceRepository.countByMIdxAndDateRange(mIdx, startTime, endTime);
        if (todayCount > 0) {
            throw new IllegalStateException("Already checked in today");
        }

        // 연속 출석 일수 계산
        int streak = calculateAttendanceStreak(mIdx, timezone);

        // 출석 등록
        AttendanceEntity attendance = new AttendanceEntity();
        attendance.setMIdx(mIdx);
        attendanceRepository.save(attendance);

        // 보상 지급 (7일 연속이면 R0005, 아니면 R0004)
        String rewardCode = (streak + 1) == 7 ? "R0005" : "R0004";
        RewardPolicyEntity policy = rewardPolicyRepository.findActiveByCd(rewardCode)
                .orElseThrow(() -> new IllegalStateException("Reward policy not found"));

        grantReward(mIdx, policy.getCd(), policy.getRAmt(), "daily_attendance_check", attendance.getDacIdx());

        log.info("Attendance checked: member={}, streak={}, reward={}", mIdx, streak + 1, policy.getRAmt());

        return new AttendanceResponse(streak + 1, true);
    }

    /**
     * 연속 출석 일수 계산
     */
    private int calculateAttendanceStreak(BigInteger mIdx, String timezone) {
        // 최근 7일간의 출석 이력 조회
        ZoneId zoneId = ZoneId.of(timezone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime sevenDaysAgo = now.minusDays(7);

        LocalDateTime startTime = sevenDaysAgo.toLocalDateTime();
        LocalDateTime endTime = now.toLocalDateTime();

        List<AttendanceEntity> recentAttendance = attendanceRepository.findByMIdx(mIdx)
                .stream()
                .filter(a -> a.getCdt().isAfter(startTime) && a.getCdt().isBefore(endTime))
                .sorted((a, b) -> b.getCdt().compareTo(a.getCdt()))
                .collect(Collectors.toList());

        if (recentAttendance.isEmpty()) {
            return 0;
        }

        // 연속 일수 계산
        int streak = 1;
        LocalDate prevDate = recentAttendance.get(0).getCdt().toLocalDate();

        for (int i = 1; i < recentAttendance.size(); i++) {
            LocalDate curDate = recentAttendance.get(i).getCdt().toLocalDate();
            long daysDiff = java.time.temporal.ChronoUnit.DAYS.between(curDate, prevDate);

            if (daysDiff == 1) {
                streak++;
                prevDate = curDate;
            } else {
                break;
            }
        }

        return streak;
    }

    // ========== 룰렛 ==========

    @Override
    @Transactional(readOnly = true)
    public RouletteStatusResponse getRouletteStatus(BigInteger mIdx, String lang, String timezone) {
        // 쿠폰 조회
        RouletteCouponEntity coupon = rouletteCouponRepository.findByMIdx(mIdx).orElse(null);
        int cnt = coupon != null ? coupon.getCnt() : 0;

        // 룰렛 정책 조회
        List<RewardPolicyEntity> policies = rewardPolicyRepository.findRouletteRewardsByLang(lang);
        List<RouletteRewardInfo> policyList = policies.stream()
                .map(p -> new RouletteRewardInfo(p.getCd(), p.getDescr(), p.getRAmt(), p.getChance()))
                .collect(Collectors.toList());

        // 최근 고액 당첨자 조회
        List<Object[]> winnersData = rouletteHisRepository.findRecentWinners(new BigDecimal("500"), 10);
        List<RouletteWinnerInfo> winners = winnersData.stream()
                .map(w -> new RouletteWinnerInfo(
                        (BigInteger) w[1],
                        (String) w[4],
                        (BigDecimal) w[2],
                        ((java.sql.Timestamp) w[3]).toLocalDateTime()
                ))
                .collect(Collectors.toList());

        // 쿠폰 충전 가능 여부 확인
        if (coupon != null && coupon.getUdt() != null) {
            if (hasAlreadyRunToday(coupon.getUdt(), timezone)) {
                RouletteStatusResponse response = new RouletteStatusResponse();
                response.setCnt(cnt);
                response.setList(winners);
                response.setPolicy(policyList);
                response.setAvail("N");
                response.setTitle("룰렛 쿠폰 충전 중");
                response.setBody("이미 쿠폰을 받으셨네요.\n다음 시간까지 기다려주세요.😅");
                response.setInfo("쿠폰 수령 시간 확인\n[룰렛 돌리기] > [쿠폰 안내]");
                return response;
            }
        }

        RouletteStatusResponse response = new RouletteStatusResponse();
        response.setCnt(cnt);
        response.setList(winners);
        response.setPolicy(policyList);
        response.setAvail("Y");
        return response;
    }

    @Override
    @Transactional
    public RoulettePlayResponse playRoulette(BigInteger mIdx, String lang) {
        // 쿠폰 사용 (차감)
        int updated = rouletteCouponRepository.decrementCoupon(mIdx);
        if (updated == 0) {
            throw new IllegalStateException("No roulette coupon available");
        }

        // 룰렛 정책 조회
        List<RewardPolicyEntity> policies = rewardPolicyRepository.findRouletteRewardsByLang(lang);
        if (policies.isEmpty()) {
            throw new IllegalStateException("No roulette rewards available");
        }

        // 확률 기반 당첨 결과 선택
        RewardPolicyEntity selected = selectRouletteReward(policies);

        // 룰렛 히스토리 저장
        RouletteHisEntity his = new RouletteHisEntity();
        his.setMIdx(mIdx);
        his.setRAmt(selected.getRAmt());
        rouletteHisRepository.save(his);

        // 보상 지급
        grantReward(mIdx, selected.getCd(), selected.getRAmt(), "roulette_coupon_usage_his", his.getRcuhIdx().longValue());

        log.info("Roulette played: member={}, reward={}", mIdx, selected.getRAmt());

        // 응답 생성
        List<RouletteRewardInfo> policyList = policies.stream()
                .map(p -> new RouletteRewardInfo(p.getCd(), p.getDescr(), p.getRAmt(), p.getChance()))
                .collect(Collectors.toList());

        RouletteRewardInfo result = new RouletteRewardInfo(
                selected.getCd(),
                selected.getDescr(),
                selected.getRAmt(),
                selected.getChance()
        );

        return new RoulettePlayResponse(policyList, result);
    }

    @Override
    @Transactional
    public void addRouletteCoupon(BigInteger mIdx, String timezone) {
        RouletteCouponEntity coupon = rouletteCouponRepository.findByMIdx(mIdx).orElse(null);

        if (coupon != null && coupon.getUdt() != null) {
            if (hasAlreadyRunToday(coupon.getUdt(), timezone)) {
                throw new IllegalStateException("Already received coupon today");
            }
        }

        if (coupon == null) {
            // 신규 생성
            coupon = new RouletteCouponEntity();
            coupon.setMIdx(mIdx);
            coupon.setCnt(1);
            rouletteCouponRepository.save(coupon);
        } else {
            // 쿠폰 추가
            rouletteCouponRepository.incrementCoupon(mIdx, 1);
        }

        log.info("Roulette coupon added: member={}", mIdx);
    }

    /**
     * 확률 기반 룰렛 보상 선택
     */
    private RewardPolicyEntity selectRouletteReward(List<RewardPolicyEntity> policies) {
        // 전체 확률 합계 계산
        BigDecimal totalChance = policies.stream()
                .map(RewardPolicyEntity::getChance)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 랜덤 값 생성 (0.0 ~ totalChance)
        double randomValue = random.nextDouble() * totalChance.doubleValue();

        // 누적 확률로 선택
        double cumulative = 0.0;
        for (RewardPolicyEntity policy : policies) {
            cumulative += policy.getChance().doubleValue();
            if (randomValue <= cumulative) {
                return policy;
            }
        }

        // 기본값 (마지막 항목)
        return policies.get(policies.size() - 1);
    }

    /**
     * 오늘 이미 실행했는지 확인
     */
    private boolean hasAlreadyRunToday(LocalDateTime lastRun, String timezone) {
        ZoneId zoneId = ZoneId.of(timezone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime lastRunZoned = lastRun.atZone(ZoneId.of("UTC")).withZoneSameInstant(zoneId);

        // 3시간마다 쿠폰 충전 가능 (시간 슬롯: 0-3, 3-6, 6-9, 9-12, 12-15, 15-18, 18-21, 21-24)
        int currentSlot = now.getHour() / 3;
        int lastRunSlot = lastRunZoned.getHour() / 3;

        return now.toLocalDate().equals(lastRunZoned.toLocalDate()) && currentSlot == lastRunSlot;
    }

    // ========== 운세 ==========

    @Override
    @Transactional(readOnly = true)
    public HoroscopeResponse getDailyHoroscope(BigInteger mIdx, String lang, String timezone) {
        // TODO: 사용자의 생년월일을 조회하여 별자리/띠 계산
        // 임시로 하드코딩된 별자리 사용
        String westernSign = "Aries";  // 예: 양자리
        String easternSign = "Rat";    // 예: 쥐띠

        // 오늘의 운세 조회
        HoroscopeDailyEntity western = horoscopeDailyRepository
                .findTodayWesternHoroscope(westernSign, lang)
                .orElse(null);

        HoroscopeDailyEntity eastern = horoscopeDailyRepository
                .findTodayEasternHoroscope(easternSign, lang)
                .orElse(null);

        // 오늘 운세 조회 여부 확인
        ZoneId zoneId = ZoneId.of(timezone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime startOfDay = now.toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime endOfDay = startOfDay.plusDays(1);

        Long todayCount = horoscopeHisRepository.countByMIdxAndDateRange(
                mIdx, startOfDay.toLocalDateTime(), endOfDay.toLocalDateTime()
        );
        boolean isComplete = todayCount > 0;

        HoroscopeInfo westernInfo = western != null
                ? new HoroscopeInfo(western.getSign(), western.getContents())
                : new HoroscopeInfo(westernSign, "No horoscope available");

        HoroscopeInfo easternInfo = eastern != null
                ? new HoroscopeInfo(eastern.getSign() + (lang.equals("KO") ? "띠" : ""), eastern.getContents())
                : new HoroscopeInfo(easternSign, "No horoscope available");

        return new HoroscopeResponse(westernInfo, easternInfo, "♈", "🐭", isComplete);
    }

    @Override
    @Transactional
    public BigDecimal claimHoroscopeReward(BigInteger mIdx, String timezone) {
        // 오늘 이미 받았는지 확인
        ZoneId zoneId = ZoneId.of(timezone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime startOfDay = now.toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime endOfDay = startOfDay.plusDays(1);

        Long todayCount = horoscopeHisRepository.countByMIdxAndDateRange(
                mIdx, startOfDay.toLocalDateTime(), endOfDay.toLocalDateTime()
        );

        if (todayCount > 0) {
            throw new IllegalStateException("Already claimed horoscope reward today");
        }

        // 운세 조회 히스토리 저장
        HoroscopeHisEntity his = new HoroscopeHisEntity();
        his.setMIdx(mIdx);
        horoscopeHisRepository.save(his);

        // 보상 지급
        RewardPolicyEntity policy = rewardPolicyRepository.findActiveByCd("R0006")
                .orElseThrow(() -> new IllegalStateException("Horoscope reward policy not found"));

        grantReward(mIdx, policy.getCd(), policy.getRAmt(), "horoscope_his", his.getHhIdx().longValue());

        log.info("Horoscope reward claimed: member={}, amount={}", mIdx, policy.getRAmt());

        return policy.getRAmt();
    }

    // ========== 공통 보상 지급 ==========

    /**
     * 보상 지급
     */
    private void grantReward(BigInteger mIdx, String code, BigDecimal amount, String tableName, Long tableIdx) {
        // 1. 보상 잔액 업데이트
        RewardBalanceEntity balance = rewardBalanceRepository.findByMIdx(mIdx)
                .orElseGet(() -> {
                    RewardBalanceEntity newBalance = new RewardBalanceEntity();
                    newBalance.setMIdx(mIdx);
                    newBalance.setSumAmt(BigDecimal.ZERO);
                    return newBalance;
                });

        balance.setSumAmt(balance.getSumAmt().add(amount));
        rewardBalanceRepository.save(balance);

        // 2. 보상 교환 이력 저장
        RewardExchangeEntity exchange = new RewardExchangeEntity();
        exchange.setMIdx(mIdx);
        exchange.setCd(code);
        exchange.setRAmt(amount);
        exchange.setTableNm(tableName);
        exchange.setTableIdx(BigInteger.valueOf(tableIdx));
        rewardExchangeRepository.save(exchange);

        log.info("Reward granted: member={}, code={}, amount={}", mIdx, code, amount);
    }
}
