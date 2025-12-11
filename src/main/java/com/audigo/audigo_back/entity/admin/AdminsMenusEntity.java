package com.audigo.audigo_back.entity.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 관리자 메뉴 정의 엔티티
 * 테이블: users.admins_menus
 */
@Entity
@Table(name = "admins_menus", schema = "users")
@Getter
@Setter
@NoArgsConstructor
public class AdminsMenusEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "am_idx")
    private BigInteger amIdx; // 메뉴 고유 ID

    @Column(name = "menu_cd", nullable = false, unique = true, length = 50)
    private String menuCd; // 메뉴 코드

    @Column(name = "depth1", length = 100)
    private String depth1; // 1차 메뉴명

    @Column(name = "depth2", length = 100)
    private String depth2; // 2차 메뉴명

    @Column(name = "depth3", length = 100)
    private String depth3; // 3차 메뉴명

    @Column(name = "menu_url", length = 255)
    private String menuUrl; // 메뉴 URL

    @Column(name = "stts", nullable = false, length = 1)
    private String stts = "1"; // 상태 (1:활성, 0:비활성)

    @Column(name = "cdt", nullable = false)
    private LocalDateTime cdt; // 생성일시

    @PrePersist
    protected void onCreate() {
        cdt = LocalDateTime.now();
    }
}
