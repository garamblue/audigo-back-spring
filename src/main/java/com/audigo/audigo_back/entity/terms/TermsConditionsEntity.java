package com.audigo.audigo_back.entity.terms;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 약관 Entity (users.terms_conditions)
 */
@Entity
@Table(name = "terms_conditions", schema = "users")
@Getter
@Setter
public class TermsConditionsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tnc_idx")
    private Long tncIdx;

    @Column(name = "tp", length = 5, nullable = false)
    private String tp; // T0001=이용약관, T0002=개인정보처리방침, T0003=푸시알림, T0004=야간푸시, T0005=이메일, T0006=Web3

    @Column(name = "tc_vers", nullable = false)
    private Integer tcVers; // 버전

    @Column(name = "region_cd", length = 2, nullable = false)
    private String regionCd; // 국가 코드

    @Column(name = "apply_dt")
    private LocalDateTime applyDt; // 적용 일시

    @Column(name = "contents", columnDefinition = "TEXT")
    private String contents; // 약관 내용

    @Column(name = "cdt")
    private LocalDateTime cdt;
}
