package com.audigo.audigo_back.entity.market;

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
@Table(name = "gift_balance", schema = "store")
public class GiftBalanceEntity {

    @Id
    @Column(name = "gb_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger gbIdx;

    @Column(name = "balance_amt", precision = 15, scale = 2)
    private BigDecimal balanceAmt;  // GiftiShow business account balance

    @Column(name = "status", nullable = false)
    private Integer status;  // 0 (OK >2M), 1 (WARNING 500K-2M), 2 (CRITICAL <500K)

    @Column(name = "cdt", nullable = false, updatable = false)
    private LocalDateTime cdt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (cdt == null) {
            cdt = LocalDateTime.now();
        }
    }
}
