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
@Table(name = "reward_balance", schema = "rwds")
public class RewardBalanceEntity {

    @Id
    @Column(name = "rb_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger rbIdx;

    @Column(name = "m_idx", nullable = false, unique = true)
    private BigInteger mIdx;

    @Column(name = "sum_amt", nullable = false, precision = 15, scale = 2)
    private BigDecimal sumAmt = BigDecimal.ZERO;

    @Column(name = "cdt", nullable = false, updatable = false)
    private LocalDateTime cdt = LocalDateTime.now();

    @UpdateTimestamp
    @Column(name = "udt")
    private LocalDateTime udt;

    @PrePersist
    protected void onCreate() {
        if (cdt == null) {
            cdt = LocalDateTime.now();
        }
        if (sumAmt == null) {
            sumAmt = BigDecimal.ZERO;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        udt = LocalDateTime.now();
    }
}
