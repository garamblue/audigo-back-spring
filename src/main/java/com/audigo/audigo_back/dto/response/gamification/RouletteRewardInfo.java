package com.audigo.audigo_back.dto.response.gamification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 룰렛 보상 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouletteRewardInfo {
    private String cd;
    private String descr;
    private BigDecimal rAmt;
    private BigDecimal chance;
}
