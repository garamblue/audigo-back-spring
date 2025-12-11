package com.audigo.audigo_back.entity.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 회원 로그인 이력 Entity (users.members_log)
 */
@Entity
@Table(name = "members_log", schema = "users")
@Getter
@Setter
public class MembersLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ml_idx")
    private Long mlIdx;

    @Column(name = "m_idx", nullable = false)
    private BigInteger mIdx;

    @Column(name = "ip", length = 50)
    private String ip;

    @Column(name = "os", length = 20)
    private String os; // iOS, Android

    @Column(name = "os_vers", length = 20)
    private String osVers;

    @Column(name = "app_vers", length = 20)
    private String appVers;

    @Column(name = "device_info", length = 100)
    private String deviceInfo;

    @Column(name = "cdt")
    private LocalDateTime cdt;
}
