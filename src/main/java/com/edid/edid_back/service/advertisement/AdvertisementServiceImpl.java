package com.audigo.audigo_back.service.advertisement;

import com.audigo.audigo_back.dto.response.advertisement.AdsAudioResponse;
import com.audigo.audigo_back.dto.response.advertisement.AdsResponseResult;
import com.audigo.audigo_back.entity.advertisement.*;
import com.audigo.audigo_back.entity.reward.RewardBalanceEntity;
import com.audigo.audigo_back.entity.reward.RewardExchangeEntity;
import com.audigo.audigo_back.repository.advertisement.*;
import com.audigo.audigo_back.repository.reward.RewardBalanceRepository;
import com.audigo.audigo_back.repository.reward.RewardExchangeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 광고 서비스 구현
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AdvertisementServiceImpl implements AdvertisementService {

    private final AdsAudioRepository adsAudioRepository;
    private final AdsSendHisRepository adsSendHisRepository;
    private final AdsResponseHisRepository adsResponseHisRepository;
    private final AdsRewardSettingsRepository adsRewardSettingsRepository;
    private final RewardBalanceRepository rewardBalanceRepository;
    private final RewardExchangeRepository rewardExchangeRepository;

    private static final int DAILY_AD_LIMIT = 30;

    /**
     * 광고 배포
     */
    @Override
    @Transactional
    public List<AdsAudioResponse> distributeAds(BigInteger mIdx, String timezone) {
        // 1. 타임존 기반 당일 시작/종료 시간 계산
        ZoneId zoneId = ZoneId.of(timezone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime startOfDay = now.toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime endOfDay = startOfDay.plusDays(1);

        LocalDateTime startTime = startOfDay.toLocalDateTime();
        LocalDateTime endTime = endOfDay.toLocalDateTime();

        // 2. 당일 이미 응답한 광고 수 확인
        Long respondedCount = adsResponseHisRepository.countByMIdxAndDateRange(mIdx, startTime, endTime);
        if (respondedCount >= DAILY_AD_LIMIT) {
            log.info("Daily ad limit reached for member: {}", mIdx);
            return new ArrayList<>();
        }

        // 3. 이미 응답한 광고 ID 목록 조회
        List<Long> respondedAdIds = adsResponseHisRepository.findRespondedAdIdsByMIdx(mIdx, startTime, endTime);

        // 4. 활성화된 광고 중 아직 응답하지 않은 광고 조회
        List<AdsAudioEntity> allActiveAds = adsAudioRepository.findAllActiveAds();
        List<AdsAudioEntity> availableAds = allActiveAds.stream()
                .filter(ad -> !respondedAdIds.contains(ad.getAaIdx()))
                .limit(DAILY_AD_LIMIT - respondedCount)
                .collect(Collectors.toList());

        // 5. 광고 배포 히스토리 저장
        List<AdsAudioResponse> responses = new ArrayList<>();
        for (AdsAudioEntity ad : availableAds) {
            AdsSendHisEntity sendHis = new AdsSendHisEntity();
            sendHis.setAaIdx(ad.getAaIdx());
            sendHis.setMIdx(mIdx);
            adsSendHisRepository.save(sendHis);

            // Response 생성
            AdsAudioResponse response = mapToResponse(ad, sendHis.getAshIdx());
            responses.add(response);
        }

        log.info("Distributed {} ads to member: {}", responses.size(), mIdx);
        return responses;
    }

    /**
     * 광고 응답 처리
     */
    @Override
    @Transactional
    public AdsResponseResult submitAdResponse(BigInteger mIdx, Long ashIdx, String answer) {
        // 1. 배포 히스토리 확인
        AdsSendHisEntity sendHis = adsSendHisRepository.findById(ashIdx)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ad send history ID"));

        if (!sendHis.getMIdx().equals(mIdx)) {
            throw new IllegalArgumentException("Ad send history does not belong to this member");
        }

        // 2. 이미 응답했는지 확인
        if (adsResponseHisRepository.findByAshIdx(ashIdx).isPresent()) {
            throw new IllegalStateException("Ad already responded");
        }

        // 3. 광고 정보 조회
        AdsAudioEntity ad = adsAudioRepository.findById(sendHis.getAaIdx())
                .orElseThrow(() -> new IllegalArgumentException("Ad not found"));

        // 4. 보상 설정 조회
        String mode = ad.getMode();
        AdsRewardSettingsEntity rewardSettings = adsRewardSettingsRepository.findActiveByMode(mode)
                .orElse(null);

        BigDecimal rewardAmount = BigDecimal.ZERO;
        if (rewardSettings != null) {
            rewardAmount = rewardSettings.getRAmt();
        } else if (ad.getRAmt() != null) {
            rewardAmount = ad.getRAmt();
        }

        // 5. 응답 히스토리 저장
        AdsResponseHisEntity responseHis = new AdsResponseHisEntity();
        responseHis.setAshIdx(ashIdx);
        responseHis.setAaIdx(ad.getAaIdx());
        responseHis.setMIdx(mIdx);
        responseHis.setMode(mode);
        responseHis.setAnswer(answer);
        adsResponseHisRepository.save(responseHis);

        // 6. 보상금 지급
        if (rewardAmount.compareTo(BigDecimal.ZERO) > 0) {
            grantReward(mIdx, rewardAmount, ad.getAaIdx(), mode);
        }

        // 7. 결과 반환
        AdsResponseResult result = new AdsResponseResult();
        result.setArhIdx(responseHis.getArhIdx());
        result.setAaIdx(ad.getAaIdx());
        result.setMode(mode);
        result.setRewardAmount(rewardAmount);
        result.setResponseDate(responseHis.getCdt());

        log.info("Ad response submitted: member={}, ad={}, reward={}", mIdx, ad.getAaIdx(), rewardAmount);
        return result;
    }

    /**
     * 남은 광고 시청 가능 횟수 조회
     */
    @Override
    @Transactional(readOnly = true)
    public int getRemainingAdCount(BigInteger mIdx, String timezone) {
        ZoneId zoneId = ZoneId.of(timezone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime startOfDay = now.toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime endOfDay = startOfDay.plusDays(1);

        LocalDateTime startTime = startOfDay.toLocalDateTime();
        LocalDateTime endTime = endOfDay.toLocalDateTime();

        Long respondedCount = adsResponseHisRepository.countByMIdxAndDateRange(mIdx, startTime, endTime);
        return Math.max(0, DAILY_AD_LIMIT - respondedCount.intValue());
    }

    /**
     * 광고 상세 조회
     */
    @Override
    @Transactional(readOnly = true)
    public AdsAudioResponse getAdDetail(Long aaIdx) {
        AdsAudioEntity ad = adsAudioRepository.findActiveAdById(aaIdx)
                .orElseThrow(() -> new IllegalArgumentException("Ad not found or inactive"));
        return mapToResponse(ad, null);
    }

    /**
     * 광고 시청 이력 조회
     */
    @Override
    @Transactional(readOnly = true)
    public List<AdsResponseResult> getAdHistory(BigInteger mIdx) {
        List<AdsResponseHisEntity> history = adsResponseHisRepository.findByMIdx(mIdx);

        return history.stream().map(h -> {
            AdsResponseResult result = new AdsResponseResult();
            result.setArhIdx(h.getArhIdx());
            result.setAaIdx(h.getAaIdx());
            result.setMode(h.getMode());
            result.setResponseDate(h.getCdt());

            // 보상금 정보는 별도 조회 필요시 추가
            return result;
        }).collect(Collectors.toList());
    }

    /**
     * 보상금 지급 (내부 메소드)
     */
    private void grantReward(BigInteger mIdx, BigDecimal amount, Long aaIdx, String mode) {
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
        exchange.setCd("ADS");  // 광고 보상
        exchange.setRAmt(amount);
        exchange.setTableNm("ads_response_his");
        exchange.setTableIdx(BigInteger.valueOf(aaIdx));
        rewardExchangeRepository.save(exchange);

        log.info("Reward granted: member={}, amount={}, mode={}", mIdx, amount, mode);
    }

    /**
     * Entity를 Response DTO로 변환
     */
    private AdsAudioResponse mapToResponse(AdsAudioEntity ad, Long ashIdx) {
        AdsAudioResponse response = new AdsAudioResponse();
        response.setAaIdx(ad.getAaIdx());
        response.setAshIdx(ashIdx);
        response.setTitle(ad.getTitle());
        response.setTitleEn(ad.getTitleEn());
        response.setContent(ad.getContent());
        response.setContentEn(ad.getContentEn());
        response.setTp(ad.getTp());
        response.setSourceUrl(ad.getSourceUrl());
        response.setImgUrl(ad.getImgUrl());
        response.setKeyword(ad.getKeyword());
        response.setRAmt(ad.getRAmt());
        response.setMode(ad.getMode());
        return response;
    }
}
