package com.audigo.audigo_back.entity.gamification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 일일 운세 데이터
 * 테이블: fun.horoscope_daily
 */
@Entity
@Table(name = "horoscope_daily", schema = "fun")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoroscopeDailyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hd_idx")
    private Long hdIdx;

    @Column(name = "date", nullable = false)
    private LocalDate date;  // 운세 날짜

    @Column(name = "sign", length = 20, nullable = false)
    private String sign;  // 별자리/띠

    @Column(name = "tp", length = 1, nullable = false)
    private String tp;  // Z=서양별자리, A=동양띠

    @Column(name = "lang", length = 2, nullable = false)
    private String lang;  // KO, EN

    @Column(name = "contents", columnDefinition = "TEXT")
    private String contents;  // 운세 내용

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
