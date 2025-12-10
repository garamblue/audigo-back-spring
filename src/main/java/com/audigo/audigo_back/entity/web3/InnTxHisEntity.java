package com.audigo.audigo_back.entity.web3;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 내부 토큰 전송 히스토리
 * 테이블: ewlt.ewallets_inn_tx_his
 */
@Entity
@Table(name = "ewallets_inn_tx_his", schema = "ewlt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InnTxHisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eith_idx")
    private Long eithIdx;

    @Column(name = "tx_hash", length = 66, nullable = false)
    private String txHash;  // 트랜잭션 해시

    @Column(name = "ew_idx", nullable = false)
    private Long ewIdx;  // 지갑 ID

    @Column(name = "from_addr", length = 42)
    private String fromAddr;  // 보내는 주소

    @Column(name = "amt", precision = 30, scale = 10, nullable = false)
    private BigDecimal amt;  // 전송 금액

    @Column(name = "status", length = 20, nullable = false)
    private String status = "pending";  // pending, success, failed

    @Column(name = "ex_rt", precision = 10, scale = 2)
    private BigDecimal exRt;  // 환율

    @Column(name = "coin_rt", precision = 10, scale = 2)
    private BigDecimal coinRt;  // 코인 시세

    @Column(name = "cd", length = 10)
    private String cd;  // 전송 코드 (T001001=signup, T001002=swap 등)

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
