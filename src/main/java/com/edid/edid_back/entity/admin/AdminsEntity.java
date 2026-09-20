package com.edid.edid_back.entity.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 관리자 정보 엔티티
 * 테이블: users.admins
 */
@Entity
@Table(name = "admins", schema = "users")
@Getter
@Setter
@NoArgsConstructor
public class AdminsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "a_idx")
    private BigInteger aIdx; // 관리자 고유 ID

    @Column(name = "id", nullable = false, unique = true, length = 255)
    private String id; // 로그인 ID (암호화 저장)

    @Column(name = "pw", nullable = false, length = 255)
    private String pw; // 비밀번호 (bcrypt 해시)

    @Column(name = "nm", nullable = false, length = 100)
    private String nm; // 이름

    @Column(name = "mobile", length = 20)
    private String mobile; // 휴대폰 번호

    @Column(name = "role_cd", nullable = false, length = 50)
    private String roleCd; // 역할 코드

    @Column(name = "act_yn", nullable = false, length = 1)
    private String actYn = "Y"; // 활성 여부 (Y/N)

    @Column(name = "cdt", nullable = false)
    private LocalDateTime cdt; // 생성일시

    @Column(name = "udt")
    private LocalDateTime udt; // 수정일시

    @PrePersist
    protected void onCreate() {
        cdt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        udt = LocalDateTime.now();
    }
}
