package com.audigo.audigo_back.entity.market;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gift_brand", schema = "store")
public class GiftBrandEntity {

    @Id
    @Column(name = "brand_code", length = 50)
    private String brandCode;

    @Column(name = "brand_seq")
    private BigInteger brandSeq;

    @Column(name = "category1_seq", nullable = false)
    private BigInteger category1Seq;

    @Column(name = "category1_name", length = 100)
    private String category1Name;

    @Column(name = "brand_name", length = 200)
    private String brandName;

    @Column(name = "brand_icon_img", length = 500)
    private String brandIconImg;

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
    }

    @PreUpdate
    protected void onUpdate() {
        udt = LocalDateTime.now();
    }
}
