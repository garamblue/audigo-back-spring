package com.audigo.audigo_back.dto.response.advertisement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 광고 정보 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdsAudioResponse {

    private Long aaIdx;           // 광고 ID
    private Long ashIdx;          // 광고 배포 ID
    private String title;         // 제목
    private String titleEn;       // 제목 (영문)
    private String content;       // 내용
    private String contentEn;     // 내용 (영문)
    private String tp;            // 타입 (AO=Audio Only, AV=Audio+Video)
    private String sourceUrl;     // 광고 파일 URL
    private String imgUrl;        // 이미지 URL
    private String keyword;       // 키워드
    private BigDecimal rAmt;      // 보상금액
    private String mode;          // 모드 (IT=Interval, CT=Continuous)
}
