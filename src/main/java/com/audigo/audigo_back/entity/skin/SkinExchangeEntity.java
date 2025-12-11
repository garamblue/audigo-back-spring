package com.audigo.audigo_back.entity.skin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 스킨 교환 Entity (fun.skin_exchange)
 */
@Entity
@Table(name = "skin_exchange", schema = "fun")
@Getter
@Setter
public class SkinExchangeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "se_idx")
    private Long seIdx;

    @Column(name = "m_idx", nullable = false)
    private BigInteger mIdx;

    @Column(name = "sl_idx", nullable = false)
    private Long slIdx; // 스킨 리스트 ID

    @Column(name = "keep_yn", length = 1)
    private String keepYn = "N"; // Y=보관함, N=미보관

    @Column(name = "ordr")
    private Integer ordr; // 순서

    @Column(name = "cdt")
    private LocalDateTime cdt;
}
