package com.audigo.audigo_back.entity.advertisement;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 광고 보상 설정
 * 테이블: ads.ads_reward_settings
 */
@Entity
@Table(name = "ads_reward_settings", schema = "ads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdsRewardSettingsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ars_idx")
    private Long arsIdx;

    @Column(name = "mode", length = 2, nullable = false)
    private String mode;  // IT=Interval, CT=Continuous

    @Column(name = "r_amt", precision = 15, scale = 2, nullable = false)
    private BigDecimal rAmt;  // 보상금액

    @Column(name = "stts", length = 1, nullable = false)
    private String stts = "Y";  // Y=활성, N=비활성

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
