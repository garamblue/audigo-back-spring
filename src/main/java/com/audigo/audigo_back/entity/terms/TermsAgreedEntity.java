package com.audigo.audigo_back.entity.terms;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 약관 동의 이력 Entity (users.terms_agreed)
 */
@Entity
@Table(name = "terms_agreed", schema = "users")
@Getter
@Setter
public class TermsAgreedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ta_idx")
    private Long taIdx;

    @Column(name = "m_idx", nullable = false)
    private BigInteger mIdx;

    @Column(name = "tnc_idx", nullable = false)
    private Long tncIdx; // 약관 ID

    @Column(name = "agreed", length = 1, nullable = false)
    private String agreed; // Y=동의, N=비동의

    @Column(name = "cdt")
    private LocalDateTime cdt;
}
