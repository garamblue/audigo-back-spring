package com.audigo.audigo_back.entity.external;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Pincrux 오퍼월 히스토리
 * 테이블: external.pincrux_offer_his
 */
@Entity
@Table(name = "pincrux_offer_his", schema = "external")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PincruxOfferEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "epoh_idx")
    private Long epohIdx;

    @Column(name = "appkey", length = 100)
    private String appkey;

    @Column(name = "pubkey")
    private Integer pubkey;

    @Column(name = "usrkey", length = 100, nullable = false)
    private String usrkey;  // 유저 고유 키

    @Column(name = "app_title", length = 200)
    private String appTitle;

    @Column(name = "coin", nullable = false)
    private Integer coin;  // 보상 코인

    @Column(name = "transid", length = 100)
    private String transid;

    @Column(name = "resign_flag", length = 1)
    private String resignFlag;

    @Column(name = "commission", length = 50)
    private String commission;

    @Column(name = "cdt", nullable = false)
    private LocalDateTime cdt;

    @PrePersist
    protected void onCreate() {
        cdt = LocalDateTime.now();
    }
}
