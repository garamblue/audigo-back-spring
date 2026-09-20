package com.edid.edid_back.entity.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 관리자별 커스텀 메뉴 권한 엔티티
 * 테이블: users.admins_roles_menus_custom
 *
 * 역할 권한을 오버라이드하는 개인별 권한 설정
 */
@Entity
@Table(name = "admins_roles_menus_custom", schema = "users")
@Getter
@Setter
@NoArgsConstructor
public class AdminsRolesMenusCustomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "armc_idx")
    private BigInteger armcIdx; // 커스텀 권한 고유 ID

    @Column(name = "a_idx", nullable = false)
    private BigInteger aIdx; // 관리자 ID

    @Column(name = "menu_cd", nullable = false, length = 50)
    private String menuCd; // 메뉴 코드

    @Column(name = "detail", nullable = false, length = 1)
    private String detail = "N"; // 조회 권한 (Y/N)

    @Column(name = "post", nullable = false, length = 1)
    private String post = "N"; // 등록 권한 (Y/N)

    @Column(name = "update", nullable = false, length = 1)
    private String update = "N"; // 수정 권한 (Y/N)

    @Column(name = "delete", nullable = false, length = 1)
    private String delete = "N"; // 삭제 권한 (Y/N)

    @Column(name = "fn", nullable = false, length = 1)
    private String fn = "N"; // 특수 기능 권한 (Y/N)

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
