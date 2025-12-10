package com.audigo.audigo_back.service.external;

import com.audigo.audigo_back.dto.request.external.PincruxRewardRequest;
import com.audigo.audigo_back.dto.response.external.LinkPriceBannerResponse;
import com.audigo.audigo_back.entity.external.LinkPriceBannerEntity;
import com.audigo.audigo_back.entity.external.LinkPriceBannerHisEntity;
import com.audigo.audigo_back.entity.external.PincruxOfferEntity;
import com.audigo.audigo_back.entity.gamification.RewardPolicyEntity;
import com.audigo.audigo_back.entity.reward.RewardBalanceEntity;
import com.audigo.audigo_back.entity.reward.RewardExchangeEntity;
import com.audigo.audigo_back.repository.external.LinkPriceBannerHisRepository;
import com.audigo.audigo_back.repository.external.LinkPriceBannerRepository;
import com.audigo.audigo_back.repository.external.PincruxOfferRepository;
import com.audigo.audigo_back.repository.gamification.RewardPolicyRepository;
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
import java.util.List;
import java.util.stream.Collectors;

/**
 * 외부 API 서비스 구현
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExternalApiServiceImpl implements ExternalApiService {

    private final PincruxOfferRepository pincruxOfferRepository;
    private final LinkPriceBannerRepository linkPriceBannerRepository;
    private final LinkPriceBannerHisRepository linkPriceBannerHisRepository;
    private final RewardPolicyRepository rewardPolicyRepository;
    private final RewardBalanceRepository rewardBalanceRepository;
    private final RewardExchangeRepository rewardExchangeRepository;

    // ========== Pincrux ==========

    @Override
    @Transactional
    public void processPincruxReward(PincruxRewardRequest request) {
        // 1. 중복 체크
        if (pincruxOfferRepository.findByTransid(request.getTransid()).isPresent()) {
            throw new IllegalStateException("Duplicate transaction ID");
        }

        // 2. Pincrux 오퍼 히스토리 저장
        PincruxOfferEntity offer = new PincruxOfferEntity();
        offer.setAppkey(request.getAppkey());
        offer.setPubkey(request.getPubkey());
        offer.setUsrkey(request.getUsrkey());
        offer.setAppTitle(request.getApp_title());
        offer.setCoin(request.getCoin());
        offer.setTransid(request.getTransid());
        offer.setResignFlag(request.getResign_flag());
        offer.setCommission(request.getCommission());
        pincruxOfferRepository.save(offer);

        // 3. 보상 지급 (usrkey를 mIdx로 변환)
        BigDecimal rewardAmount = BigDecimal.valueOf(request.getCoin());
        // TODO: usrkey를 members 테이블에서 m_idx로 변환하는 로직 필요
        // 임시로 usrkey를 BigInteger로 변환 (실제로는 members.ext_key로 조회 필요)
        try {
            BigInteger mIdx = new BigInteger(request.getUsrkey());
            grantReward(mIdx, "R0001", rewardAmount, "pincrux_offer_his", offer.getEpohIdx());
            log.info("Pincrux reward processed: member={}, coin={}, transid={}", mIdx, request.getCoin(), request.getTransid());
        } catch (NumberFormatException e) {
            log.warn("Invalid usrkey format: {}, skipping reward", request.getUsrkey());
        }
    }

    // ========== LinkPrice ==========

    @Override
    @Transactional(readOnly = true)
    public List<LinkPriceBannerResponse> getRandomBanners() {
        List<LinkPriceBannerEntity> banners = linkPriceBannerRepository.findRandomThree();

        return banners.stream()
                .map(b -> new LinkPriceBannerResponse(
                        b.getLbIdx(),
                        b.getProductName(),
                        b.getProductUrl(),
                        b.getImageUrl(),
                        b.getPrice(),
                        b.getCategory()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BigDecimal clickLinkPriceBanner(BigInteger mIdx, Long lbIdx, String timezone) {
        // 1. 타임존 기반 당일 시작/종료 시간 계산
        ZoneId zoneId = ZoneId.of(timezone);
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime startOfDay = now.toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime endOfDay = startOfDay.plusDays(1);

        LocalDateTime startTime = startOfDay.toLocalDateTime();
        LocalDateTime endTime = endOfDay.toLocalDateTime();

        // 2. 당일 이미 클릭했는지 확인 (1일 1회 제한)
        Long todayCount = linkPriceBannerHisRepository.countByMIdxAndDateRange(mIdx, startTime, endTime);
        if (todayCount > 0) {
            throw new IllegalStateException("Already clicked banner today");
        }

        // 3. 배너 존재 확인
        LinkPriceBannerEntity banner = linkPriceBannerRepository.findById(lbIdx)
                .orElseThrow(() -> new IllegalArgumentException("Banner not found"));

        if (!"Y".equals(banner.getStts())) {
            throw new IllegalStateException("Banner is not active");
        }

        // 4. 클릭 히스토리 저장
        LinkPriceBannerHisEntity history = new LinkPriceBannerHisEntity();
        history.setLbIdx(lbIdx);
        history.setMIdx(mIdx);
        linkPriceBannerHisRepository.save(history);

        // 5. 보상 정책 조회
        RewardPolicyEntity policy = rewardPolicyRepository.findActiveByCd("R0002")
                .orElseThrow(() -> new IllegalStateException("Banner reward policy not found"));

        // 6. 보상 지급
        grantReward(mIdx, policy.getCd(), policy.getRAmt(), "linkprice_banner_his", history.getLbhIdx());

        log.info("LinkPrice banner clicked: member={}, banner={}, reward={}",
                mIdx, lbIdx, policy.getRAmt());

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
