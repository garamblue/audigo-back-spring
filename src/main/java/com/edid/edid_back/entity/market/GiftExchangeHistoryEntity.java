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
@Table(name = "gift_exchange_his", schema = "store", indexes = {
    @Index(name = "idx_m_idx", columnList = "m_idx"),
    @Index(name = "idx_tr_id", columnList = "tr_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_retran_yn", columnList = "retran_yn")
})
public class GiftExchangeHistoryEntity {

    @Id
    @Column(name = "geh_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger gehIdx;

    @Column(name = "m_idx", nullable = false)
    private BigInteger mIdx;  // Member index

    @Column(name = "gp_idx")
    private BigInteger gpIdx;  // Product index

    @Column(name = "goods_code", length = 50)
    private String goodsCode;

    @Column(name = "goods_name", length = 300)
    private String goodsName;

    @Column(name = "brand_code", length = 50)
    private String brandCode;

    @Column(name = "r_amt", precision = 15, scale = 2)
    private BigDecimal rAmt;  // Exchange reward amount

    @Column(name = "real_price", length = 50)
    private String realPrice;  // Actual product price

    @Column(name = "limit_day", length = 50)
    private String limitDay;  // Product validity

    @Column(name = "valid_prd_day", length = 50)
    private String validPrdDay;  // Validity period

    @Column(name = "mobile_num", length = 200)
    private String mobileNum;  // Recipient phone number (encrypted)

    @Column(name = "tr_id", length = 100, unique = true)
    private String trId;  // GiftiShow trade ID

    @Column(name = "order_no", length = 100)
    private String orderNo;  // GiftiShow order number

    @Column(name = "status", length = 1)
    private String status;  // S (Success), F (Failed), C (Cancelled)

    @Column(name = "retran_yn", length = 1, nullable = false)
    private String retranYn = "N";  // Y/N resend flag

    @Column(name = "req_dt")
    private LocalDateTime reqDt;  // Request timestamp

    @Column(name = "res_dt")
    private LocalDateTime resDt;  // Response/completion timestamp

    @Column(name = "cdt", nullable = false, updatable = false)
    private LocalDateTime cdt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (cdt == null) {
            cdt = LocalDateTime.now();
        }
        if (retranYn == null) {
            retranYn = "N";
        }
        if (reqDt == null) {
            reqDt = LocalDateTime.now();
        }
    }
}
