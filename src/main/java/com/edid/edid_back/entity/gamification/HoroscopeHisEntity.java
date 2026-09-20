package com.audigo.audigo_back.entity.gamification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 운세 조회 히스토리
 * 테이블: fun.horoscope_his
 */
@Entity
@Table(name = "horoscope_his", schema = "fun")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HoroscopeHisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hh_idx")
    private Long hhIdx;

    @Column(name = "m_idx", nullable = false)
    private BigInteger mIdx;  // 회원 ID

    @Column(name = "cdt", nullable = false)
    private LocalDateTime cdt;

    @PrePersist
    protected void onCreate() {
        cdt = LocalDateTime.now();
    }
}
