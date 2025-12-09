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
@Table(name = "gift_category", schema = "store")
public class GiftCategoryEntity {

    @Id
    @Column(name = "category1_seq")
    private BigInteger category1Seq;

    @Column(name = "category1_name", length = 100)
    private String category1Name;

    @Column(name = "image_url", length = 500)
    private String imageUrl;  // S3 CloudFront signed URL

    @Column(name = "visible", length = 1, nullable = false)
    private String visible = "Y";  // Y/N

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
    }

    @PreUpdate
    protected void onUpdate() {
        udt = LocalDateTime.now();
    }
}
