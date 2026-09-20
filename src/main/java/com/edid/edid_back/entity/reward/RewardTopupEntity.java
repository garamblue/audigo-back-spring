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
@Table(name = "reward_topup", schema = "rwds")
public class RewardTopupEntity {

    @Id
    @Column(name = "rt_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger rtIdx;

    @Column(name = "m_idx", nullable = false)
    private BigInteger mIdx;

    @Column(name = "cd", nullable = false, length = 10)
    private String cd;  // Reward code (R0001-R0030)

    @Column(name = "r_amt", nullable = false, precision = 15, scale = 2)
    private BigDecimal rAmt;

    @Column(name = "tran_dt", nullable = false)
    private LocalDateTime tranDt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (tranDt == null) {
            tranDt = LocalDateTime.now();
        }
    }
}
