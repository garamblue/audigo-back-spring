package com.audigo.audigo_back.entity.advertisement;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 오디오 광고 정보
 * 테이블: ads.ads_audio
 */
@Entity
@Table(name = "ads_audio", schema = "ads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AdsAudioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aa_idx")
    private Long aaIdx;

    @Column(name = "title", length = 50, nullable = false)
    private String title;

    @Column(name = "title_en", length = 50)
    private String titleEn;

    @Column(name = "content", length = 200, nullable = false)
    private String content;

    @Column(name = "content_en", length = 200)
    private String contentEn;

    @Column(name = "tp", length = 2, nullable = false)
    private String tp;  // AO=Audio Only, AV=Audio+Video

    @Column(name = "source_url", length = 200)
    private String sourceUrl;  // S3 URL

    @Column(name = "img_url", length = 200)
    private String imgUrl;

    @Column(name = "keyword", length = 200)
    private String keyword;

    @Column(name = "r_amt", precision = 15, scale = 2)
    private BigDecimal rAmt;  // 기본 보상금

    @Column(name = "mode", length = 2)
    private String mode;  // IT=Interval, CT=Continuous

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
