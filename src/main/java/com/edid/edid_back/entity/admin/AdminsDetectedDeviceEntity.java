package com.edid.edid_back.entity.admin;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 관리자 승인된 기기/IP 엔티티
 * 테이블: users.admins_detected_device
 *
 * 신규 기기/IP에서 로그인 시 승인 필요
 */
@Entity
@Table(name = "admins_detected_device", schema = "users")
@Getter
@Setter
@NoArgsConstructor
public class AdminsDetectedDeviceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "add_idx")
    private BigInteger addIdx; // 기기 승인 고유 ID

    @Column(name = "a_idx", nullable = false)
    private BigInteger aIdx; // 관리자 ID

    @Column(name = "device_id", length = 255)
    private String deviceId; // 기기 ID

    @Column(name = "ip_addr", length = 45)
    private String ipAddr; // IP 주소

    @Column(name = "user_agent", length = 500)
    private String userAgent; // User-Agent

    @Column(name = "approved_yn", nullable = false, length = 1)
    private String approvedYn = "N"; // 승인 여부 (Y/N)

    @Column(name = "approved_dt")
    private LocalDateTime approvedDt; // 승인 일시

    @Column(name = "approved_by")
    private BigInteger approvedBy; // 승인자 관리자 ID

    @Column(name = "cdt", nullable = false)
    private LocalDateTime cdt; // 최초 감지 일시

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
