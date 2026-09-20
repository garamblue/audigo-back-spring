package com.audigo.audigo_back.service.advertisement;

import com.audigo.audigo_back.dto.response.advertisement.AdsAudioResponse;
import com.audigo.audigo_back.dto.response.advertisement.AdsResponseResult;

import java.math.BigInteger;
import java.util.List;

/**
 * 광고 서비스 인터페이스
 */
public interface AdvertisementService {

    /**
     * 광고 배포 (사용자에게 광고 제공)
     * @param mIdx 회원 ID
     * @param timezone 사용자 타임존 (예: "Asia/Seoul")
     * @return 배포 가능한 광고 목록
     */
    List<AdsAudioResponse> distributeAds(BigInteger mIdx, String timezone);

    /**
     * 광고 응답 처리 (시청 완료 및 퀴즈 응답)
     * @param mIdx 회원 ID
     * @param ashIdx 광고 배포 ID
     * @param answer 퀴즈 정답 (A1, A2, A3 등)
     * @return 응답 결과 (보상금 포함)
     */
    AdsResponseResult submitAdResponse(BigInteger mIdx, Long ashIdx, String answer);

    /**
     * 사용자의 당일 광고 시청 가능 횟수 조회
     * @param mIdx 회원 ID
     * @param timezone 사용자 타임존
     * @return 남은 시청 가능 횟수
     */
    int getRemainingAdCount(BigInteger mIdx, String timezone);

    /**
     * 특정 광고 상세 조회
     * @param aaIdx 광고 ID
     * @return 광고 상세 정보
     */
    AdsAudioResponse getAdDetail(Long aaIdx);

    /**
     * 사용자의 광고 시청 이력 조회
     * @param mIdx 회원 ID
     * @return 광고 시청 이력 목록
     */
    List<AdsResponseResult> getAdHistory(BigInteger mIdx);
}
