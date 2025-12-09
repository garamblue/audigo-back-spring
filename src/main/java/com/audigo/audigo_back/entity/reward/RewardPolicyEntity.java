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
@Table(name = "reward_policy", schema = "rwds")
public class RewardPolicyEntity {

    @Id
    @Column(name = "rp_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger rpIdx;

    @Column(name = "cd", nullable = false, length = 10)
    private String cd;  // Reward code

    @Column(name = "tp", length = 1)
    private String tp;  // Type: T (Topup) or E (Exchange)

    @Column(name = "descr", length = 100)
    private String descr;  // Description

    @Column(name = "lang", nullable = false, length = 2)
    private String lang;  // Language: KO, EN

    @Column(name = "r_amt", precision = 15, scale = 2)
    private BigDecimal rAmt;  // Default reward amount

    @Column(name = "chance", precision = 5, scale = 2)
    private BigDecimal chance;  // Probability (for random rewards)

    @Column(name = "stts", length = 1, nullable = false)
    private String stts = "Y";  // Status: Y (Active), N (Inactive)

    @Column(name = "act_dt")
    private LocalDateTime actDt;  // Activation date

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
        if (stts == null) {
            stts = "Y";
        }
    }

    @PreUpdate
    protected void onUpdate() {
        udt = LocalDateTime.now();
    }
}
