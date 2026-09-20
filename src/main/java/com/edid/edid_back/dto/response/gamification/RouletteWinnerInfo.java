package com.audigo.audigo_back.dto.response.gamification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 룰렛 당첨자 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouletteWinnerInfo {
    private BigInteger mIdx;
    private String nickname;
    private BigDecimal rAmt;
    private LocalDateTime cdt;
}
