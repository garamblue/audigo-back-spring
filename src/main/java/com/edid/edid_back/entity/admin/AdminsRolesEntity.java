package com.audigo.audigo_back.entity.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 관리자 역할 정의 엔티티
 * 테이블: users.admins_roles
 */
@Entity
@Table(name = "admins_roles", schema = "users")
@Getter
@Setter
@NoArgsConstructor
public class AdminsRolesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ar_idx")
    private BigInteger arIdx; // 역할 고유 ID

    @Column(name = "role_cd", nullable = false, unique = true, length = 50)
    private String roleCd; // 역할 코드 (SUPER_ADMIN, ADMIN, VIEWER 등)

    @Column(name = "role_nm", nullable = false, length = 100)
    private String roleNm; // 역할 이름

    @Column(name = "stts", nullable = false, length = 1)
    private String stts = "1"; // 상태 (1:활성, 0:비활성)

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
