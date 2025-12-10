package com.audigo.audigo_back.entity.web3;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 스왑 수수료 설정
 * 테이블: ewlt.reward_swap_fee
 */
@Entity
@Table(name = "reward_swap_fee", schema = "ewlt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SwapFeeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rcr_idx")
    private Long rcrIdx;

    @Column(name = "ratio", precision = 5, scale = 2, nullable = false)
    private BigDecimal ratio;  // 수수료 비율 (예: 5.00 = 5%)

    @Column(name = "cdt", nullable = false)
    private LocalDateTime cdt;  // 생성일시

    @PrePersist
    protected void onCreate() {
        cdt = LocalDateTime.now();
    }
}
