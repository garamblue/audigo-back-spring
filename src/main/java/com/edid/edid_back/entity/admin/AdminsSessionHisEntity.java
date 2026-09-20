package com.edid.edid_back.entity.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 관리자 세션 이력 엔티티
 * 테이블: users.admins_session_his
 *
 * 로그인/로그아웃 세션 추적
 */
@Entity
@Table(name = "admins_session_his", schema = "users")
@Getter
@Setter
@NoArgsConstructor
public class AdminsSessionHisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ash_idx")
    private BigInteger ashIdx; // 세션 이력 고유 ID

    @Column(name = "a_idx", nullable = false)
    private BigInteger aIdx; // 관리자 ID

    @Column(name = "session_token", length = 500)
    private String sessionToken; // 세션 토큰 (JWT)

    @Column(name = "device_id", length = 255)
    private String deviceId; // 기기 ID

    @Column(name = "ip_addr", length = 45)
    private String ipAddr; // IP 주소 (IPv6 지원)

    @Column(name = "user_agent", length = 500)
    private String userAgent; // User-Agent

    @Column(name = "login_dt", nullable = false)
    private LocalDateTime loginDt; // 로그인 일시

    @Column(name = "logout_dt")
    private LocalDateTime logoutDt; // 로그아웃 일시

    @Column(name = "stts", nullable = false, length = 1)
    private String stts = "A"; // 세션 상태 (A:활성, E:만료, L:로그아웃)

    @PrePersist
    protected void onCreate() {
        if (loginDt == null) {
            loginDt = LocalDateTime.now();
        }
    }
}
