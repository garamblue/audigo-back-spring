package com.audigo.audigo_back.entity.scheduler;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 환율 정보
 * 테이블: exchange_ratio
 */
@Entity
@Table(name = "exchange_ratio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRatioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "er_idx")
    private Long erIdx;

    @Column(name = "us_ratio", precision = 15, scale = 2, nullable = false)
    private BigDecimal usRatio;  // USD -> KRW 환율

    @Column(name = "cdt", nullable = false)
    private LocalDateTime cdt;

    @PrePersist
    protected void onCreate() {
        cdt = LocalDateTime.now();
    }
}
