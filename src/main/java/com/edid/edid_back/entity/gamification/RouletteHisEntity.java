package com.audigo.audigo_back.entity.gamification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 룰렛 사용 히스토리
 * 테이블: fun.roulette_coupon_usage_his
 */
@Entity
@Table(name = "roulette_coupon_usage_his", schema = "fun")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouletteHisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rcuh_idx")
    private Long rcuhIdx;

    @Column(name = "m_idx", nullable = false)
    private BigInteger mIdx;  // 회원 ID

    @Column(name = "r_amt", precision = 15, scale = 2, nullable = false)
    private BigDecimal rAmt;  // 당첨 보상금

    @Column(name = "cdt", nullable = false)
    private LocalDateTime cdt;

    @PrePersist
    protected void onCreate() {
        cdt = LocalDateTime.now();
    }
}
