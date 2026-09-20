package com.audigo.audigo_back.entity.external;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * LinkPrice 배너 정보
 * 테이블: external.linkprice_banner
 */
@Entity
@Table(name = "linkprice_banner", schema = "external")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LinkPriceBannerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lb_idx")
    private Long lbIdx;

    @Column(name = "product_name", length = 200)
    private String productName;

    @Column(name = "product_url", length = 500)
    private String productUrl;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "price")
    private Integer price;

    @Column(name = "category", length = 100)
    private String category;

    @Column(name = "stts", length = 1, nullable = false)
    private String stts = "Y";  // Y=활성, N=비활성

    @Column(name = "cdt", nullable = false)
    private LocalDateTime cdt;

    @Column(name = "udt")
    private LocalDateTime udt;

    @PrePersist
    protected void onCreate() {
        cdt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        udt = LocalDateTime.now();
    }
}
