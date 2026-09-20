package com.audigo.audigo_back.dto.response.advertisement;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 광고 응답 결과 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdsResponseResult {

    private Long arhIdx;              // 응답 히스토리 ID
    private Long aaIdx;               // 광고 ID
    private String mode;              // 모드 (IT/CT)
    private BigDecimal rewardAmount;  // 지급된 보상금액
    private LocalDateTime responseDate; // 응답 일시
}
