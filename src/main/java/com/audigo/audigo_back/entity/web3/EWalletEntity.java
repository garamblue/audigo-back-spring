package com.audigo.audigo_back.entity.web3;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * EWallet 엔티티 - 사용자 지갑 정보
 * 테이블: ewlt.ewallets
 */
@Entity
@Table(name = "ewallets", schema = "ewlt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EWalletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ew_idx")
    private Long ewIdx;

    @Column(name = "m_idx", nullable = false)
    private java.math.BigInteger mIdx;  // 회원 ID

    @Column(name = "addr", length = 42)
    private String addr;  // 지갑 주소 (0x...)

    @Column(name = "server_key", length = 100)
    private String serverKey;  // 서버 키

    @Column(name = "token_amt", precision = 30, scale = 10)
    private BigDecimal tokenAmt = BigDecimal.ZERO;  // 토큰 잔액

    @Column(name = "bnb_amt", precision = 30, scale = 10)
    private BigDecimal bnbAmt = BigDecimal.ZERO;  // BNB 잔액

    @Column(name = "cdt", nullable = false)
    private LocalDateTime cdt;  // 생성일시

    @Column(name = "udt")
    private LocalDateTime udt;  // 수정일시

    @PrePersist
    protected void onCreate() {
        cdt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        udt = LocalDateTime.now();
    }
}
