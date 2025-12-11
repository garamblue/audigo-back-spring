package com.audigo.audigo_back.entity.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 회원 Entity (users.members)
 */
@Entity
@Table(name = "members", schema = "users")
@Getter
@Setter
public class MembersEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "m_idx")
    private BigInteger mIdx;

    @Column(name = "stts", length = 1, nullable = false)
    private String stts; // 1=일반회원, 2=정지, 3=탈퇴, 4=미성년자

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "nickname", length = 50, nullable = false)
    private String nickname;

    @Column(name = "birth_dt")
    private LocalDate birthDt;

    @Column(name = "gender", length = 1)
    private String gender; // M/F

    @Column(name = "state", length = 2)
    private String state; // 국가 코드

    @Column(name = "mobile_num", length = 20)
    private String mobileNum;

    @Column(name = "invit_cd", length = 12, unique = true)
    private String invitCd; // 초대 코드

    @Column(name = "ext_key", length = 16, unique = true)
    private String extKey; // 외부 키 (Pincrux 등)

    @Column(name = "cdt")
    private LocalDateTime cdt; // 생성일시

    @Column(name = "lv_dt")
    private LocalDateTime lvDt; // 탈퇴일시
}
