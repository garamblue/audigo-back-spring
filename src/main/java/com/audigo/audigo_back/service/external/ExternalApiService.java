package com.audigo.audigo_back.service.external;

import com.audigo.audigo_back.dto.request.external.PincruxRewardRequest;
import com.audigo.audigo_back.dto.response.external.LinkPriceBannerResponse;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

/**
 * 외부 API 서비스 인터페이스
 */
public interface ExternalApiService {

    // ========== Pincrux ==========

    /**
     * Pincrux 오퍼월 보상 처리
     */
    void processPincruxReward(PincruxRewardRequest request);

    // ========== LinkPrice ==========

    /**
     * LinkPrice 배너 3개 조회
     */
    List<LinkPriceBannerResponse> getRandomBanners();

    /**
     * LinkPrice 배너 클릭 보상
     */
    BigDecimal clickLinkPriceBanner(BigInteger mIdx, Long lbIdx, String timezone);
}
