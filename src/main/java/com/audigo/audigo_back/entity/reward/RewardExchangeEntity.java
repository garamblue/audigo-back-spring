package com.audigo.audigo_back.entity.reward;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reward_exchange", schema = "rwds")
public class RewardExchangeEntity {

    @Id
    @Column(name = "re_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger reIdx;

    @Column(name = "m_idx", nullable = false)
    private BigInteger mIdx;

    @Column(name = "cd", nullable = false, length = 10)
    private String cd;  // Exchange code (R0000, R0021-R0023, R0027)

    @Column(name = "r_amt", nullable = false, precision = 15, scale = 2)
    private BigDecimal rAmt;

    @Column(name = "table_idx")
    private BigInteger tableIdx;  // Reference to source table

    @Column(name = "table_nm", length = 20)
    private String tableNm;  // Source table code (C001001-C001012)

    @Column(name = "tran_dt", nullable = false)
    private LocalDateTime tranDt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (tranDt == null) {
            tranDt = LocalDateTime.now();
        }
    }
}
