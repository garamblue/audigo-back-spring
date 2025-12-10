package com.audigo.audigo_back.entity.gamification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 보상 정책
 * 테이블: rwds.reward_policy
 */
@Entity
@Table(name = "reward_policy", schema = "rwds")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RewardPolicyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rp_idx")
    private Long rpIdx;

    @Column(name = "cd", length = 10, nullable = false, unique = true)
    private String cd;  // 보상 코드 (R0004=출석, R0005=7일출석, R0006=운세, RL=룰렛)

    @Column(name = "cd_tp", length = 10)
    private String cdTp;  // 코드 타입 (RL=룰렛)

    @Column(name = "descr", length = 100)
    private String descr;  // 설명

    @Column(name = "r_amt", precision = 15, scale = 2, nullable = false)
    private BigDecimal rAmt;  // 보상금액

    @Column(name = "chance", precision = 5, scale = 2)
    private BigDecimal chance;  // 확률 (룰렛용, %)

    @Column(name = "lang", length = 2)
    private String lang;  // 언어 (KO, EN)

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
