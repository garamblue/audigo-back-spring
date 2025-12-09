package com.audigo.audigo_back.entity.market;

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
@Table(name = "gift_product", schema = "store", indexes = {
    @Index(name = "idx_goods_code", columnList = "goods_code"),
    @Index(name = "idx_brand_code", columnList = "brand_code")
})
public class GiftProductEntity {

    @Id
    @Column(name = "gp_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger gpIdx;

    @Column(name = "goods_code", nullable = false, length = 50, unique = true)
    private String goodsCode;

    @Column(name = "goods_name", length = 300)
    private String goodsName;

    @Column(name = "brand_code", length = 50)
    private String brandCode;

    @Column(name = "category1_seq")
    private BigInteger category1Seq;

    @Column(name = "real_price", precision = 15, scale = 2)
    private BigDecimal realPrice;  // GiftiShow actual price

    @Column(name = "app_price", precision = 15, scale = 2)
    private BigDecimal appPrice;  // Price charged to user (real_price * 1.5)

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;  // Product description/HTML

    @Column(name = "limit_day", length = 50)
    private String limitDay;  // Validity/expiration period

    @Column(name = "goods_imgs", length = 500)
    private String goodsImgs;  // Small product image URL

    @Column(name = "goods_imgb", length = 500)
    private String goodsImgb;  // Large product image URL

    @Column(name = "valid_prd_day", length = 50)
    private String validPrdDay;  // Validity period days

    @Column(name = "goods_state_cd", length = 20)
    private String goodsStateCd;  // Product status code

    @Column(name = "goods_type_dtl_nm", length = 100)
    private String goodsTypeDtlNm;  // Product type detail name

    @Column(name = "affiliate", length = 100)
    private String affiliate;  // Affiliate ID

    @Column(name = "visible", length = 1, nullable = false)
    private String visible = "Y";  // Y/N visibility flag

    @Column(name = "use_yn", length = 1, nullable = false)
    private String useYn = "Y";  // Y/N usage flag

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
        if (visible == null) {
            visible = "Y";
        }
        if (useYn == null) {
            useYn = "Y";
        }
        // Calculate app_price as real_price * 1.5 (50% markup)
        if (appPrice == null && realPrice != null) {
            appPrice = realPrice.multiply(new BigDecimal("1.5"));
        }
    }

    @PreUpdate
    protected void onUpdate() {
        udt = LocalDateTime.now();
        // Recalculate app_price if real_price changed
        if (realPrice != null) {
            appPrice = realPrice.multiply(new BigDecimal("1.5"));
        }
    }
}
