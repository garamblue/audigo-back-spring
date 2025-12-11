package com.audigo.audigo_back.entity.auth;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * 휴대폰 인증 이력 Entity
 */
@Entity
@Table(name = "mobile_verify_his", schema = "users")
@Getter
@Setter
public class MobileVerifyHisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mvh_idx")
    private Long mvhIdx;

    @Column(name = "mobile_num", nullable = false, length = 20)
    private String mobileNum;

    @Column(name = "auth_cd", nullable = false, length = 10)
    private String authCd;

    @Column(name = "exp_dt")
    private LocalDateTime expDt;

    @Column(name = "exp_yn", length = 1)
    private String expYn = "N";

    @Column(name = "cdt")
    private LocalDateTime cdt;
}
