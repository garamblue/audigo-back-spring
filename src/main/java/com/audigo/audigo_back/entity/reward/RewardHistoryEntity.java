package com.audigo.audigo_back.entity.reward;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "reward_his", schema = "rwds")
public class RewardHistoryEntity {

    @Id
    @Column(name = "rh_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger rhIdx;

    @Column(name = "rt_idx")
    private BigInteger rtIdx;  // Reference to reward_topup

    @Column(name = "table_idx", nullable = false)
    private BigInteger tableIdx;  // Reference ID to source table

    @Column(name = "table_nm", nullable = false, length = 20)
    private String tableNm;  // Source table code (RewardTableCode enum)
}
