package com.audigo.audigo_back.entity.advertisement;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 광고 배포 히스토리
 * 테이블: ads.ads_send_his
 */
@Entity
@Table(name = "ads_send_his", schema = "ads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdsSendHisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ash_idx")
    private Long ashIdx;

    @Column(name = "aa_idx", nullable = false)
    private Long aaIdx;  // 광고 ID

    @Column(name = "m_idx", nullable = false)
    private BigInteger mIdx;  // 회원 ID

    @Column(name = "cdt", nullable = false)
    private LocalDateTime cdt;

    @PrePersist
    protected void onCreate() {
        cdt = LocalDateTime.now();
    }
}
