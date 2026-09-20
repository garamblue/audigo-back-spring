package com.edid.edid_back.entity.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 역할별 메뉴 권한 매핑 엔티티
 * 테이블: users.admins_roles_menus
 *
 * 권한 종류:
 * - detail: 조회 권한
 * - post: 등록 권한
 * - update: 수정 권한
 * - delete: 삭제 권한
 * - fn: 특수 기능 권한
 */
@Entity
@Table(name = "admins_roles_menus", schema = "users")
@Getter
@Setter
@NoArgsConstructor
public class AdminsRolesMenusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "arm_idx")
    private BigInteger armIdx; // 권한 매핑 고유 ID

    @Column(name = "role_cd", nullable = false, length = 50)
    private String roleCd; // 역할 코드

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
