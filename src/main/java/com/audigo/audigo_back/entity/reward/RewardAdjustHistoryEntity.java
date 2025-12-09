package com.audigo.audigo_back.entity.reward;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reward_adjust_his", schema = "rwds")
public class RewardAdjustHistoryEntity {

    @Id
    @Column(name = "rah_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger rahIdx;

    @Column(name = "m_idx", nullable = false)
    private BigInteger mIdx;

    @Column(name = "cd", nullable = false, length = 10)
    private String cd;  // Reward code

    @Column(name = "type", nullable = false, length = 1)
    private String type;  // P (Plus/Topup), M (Minus/Exchange), E (Expired)

    @Column(name = "r_amt", nullable = false, precision = 15, scale = 2)
    private BigDecimal rAmt;

    @Column(name = "tran_dt")
    private LocalDateTime tranDt;  // Transaction datetime (null = immediate, future = scheduled)

    @Column(name = "cdt", nullable = false, updatable = false)
    private LocalDateTime cdt = LocalDateTime.now();

    @UpdateTimestamp
    @Column(name = "udt")
    private LocalDateTime udt;

    @Column(name = "c_aidx")
    private BigInteger cAidx;  // Created by admin ID

    @Column(name = "u_aidx")
    private BigInteger uAidx;  // Updated by admin ID

    @PrePersist
    protected void onCreate() {
        if (cdt == null) {
            cdt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        udt = LocalDateTime.now();
    }
}
