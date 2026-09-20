package com.audigo.audigo_back.entity.market;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "gift_view_his", schema = "store", indexes = {
    @Index(name = "idx_view_m_idx", columnList = "m_idx"),
    @Index(name = "idx_view_gp_idx", columnList = "gp_idx")
})
public class GiftViewHistoryEntity {

    @Id
    @Column(name = "gvh_idx")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger gvhIdx;

    @Column(name = "m_idx", nullable = false)
    private BigInteger mIdx;  // Member index

    @Column(name = "gp_idx", nullable = false)
    private BigInteger gpIdx;  // Product index

    @Column(name = "cdt", nullable = false, updatable = false)
    private LocalDateTime cdt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (cdt == null) {
            cdt = LocalDateTime.now();
        }
    }
}
