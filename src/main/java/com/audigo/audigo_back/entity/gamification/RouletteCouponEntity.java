package com.audigo.audigo_back.entity.gamification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 룰렛 쿠폰
 * 테이블: fun.roulette_coupon_usage
 */
@Entity
@Table(name = "roulette_coupon_usage", schema = "fun")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RouletteCouponEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rco_idx")
    private Long rcoIdx;

    @Column(name = "m_idx", nullable = false, unique = true)
    private BigInteger mIdx;  // 회원 ID

    @Column(name = "cnt", nullable = false)
    private Integer cnt = 0;  // 보유 쿠폰 수

    @Column(name = "cdt", nullable = false)
    private LocalDateTime cdt;

    @Column(name = "udt")
    private LocalDateTime udt;

    @PrePersist
    protected void onCreate() {
        cdt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        udt = LocalDateTime.now();
    }
}
