package com.audigo.audigo_back.entity.advertisement;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 광고 응답 히스토리 (시청 완료 및 퀴즈 응답)
 * 테이블: ads.ads_response_his
 */
@Entity
@Table(name = "ads_response_his", schema = "ads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdsResponseHisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "arh_idx")
    private Long arhIdx;

    @Column(name = "ash_idx", nullable = false)
    private Long ashIdx;  // 광고 배포 ID

    @Column(name = "aa_idx", nullable = false)
    private Long aaIdx;  // 광고 ID

    @Column(name = "m_idx", nullable = false)
    private BigInteger mIdx;  // 회원 ID

    @Column(name = "mode", length = 2)
    private String mode;  // IT=Interval(중간), CT=Continuous(연속)

    @Column(name = "answer", length = 2)
    private String answer;  // 퀴즈 정답 (A1, A2, A3 등)

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
