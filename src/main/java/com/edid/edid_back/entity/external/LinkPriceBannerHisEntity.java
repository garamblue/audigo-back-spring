package com.audigo.audigo_back.entity.external;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * LinkPrice 배너 클릭 히스토리
 * 테이블: external.linkprice_banner_his
 */
@Entity
@Table(name = "linkprice_banner_his", schema = "external")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LinkPriceBannerHisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lbh_idx")
    private Long lbhIdx;

    @Column(name = "lb_idx", nullable = false)
    private Long lbIdx;  // 배너 ID

    @Column(name = "m_idx", nullable = false)
    private BigInteger mIdx;  // 회원 ID

    @Column(name = "cdt", nullable = false)
    private LocalDateTime cdt;

    @PrePersist
    protected void onCreate() {
        cdt = LocalDateTime.now();
    }
}
