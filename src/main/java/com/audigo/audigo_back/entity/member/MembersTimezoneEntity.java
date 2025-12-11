package com.audigo.audigo_back.entity.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 회원 타임존 Entity (users.members_timezone)
 */
@Entity
@Table(name = "members_timezone", schema = "users")
@Getter
@Setter
public class MembersTimezoneEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mtz_idx")
    private Long mtzIdx;

    @Column(name = "m_idx", nullable = false)
    private BigInteger mIdx;

    @Column(name = "mobile_tz", length = 50, nullable = false)
    private String mobileTz; // Asia/Seoul

    @Column(name = "region_cd", length = 2, nullable = false)
    private String regionCd; // KR

    @Column(name = "stts", length = 1)
    private String stts = "Y"; // Y=사용중, N=비활성

    @Column(name = "aprv_dt")
    private LocalDateTime aprvDt; // 승인일시

    @Column(name = "cdt")
    private LocalDateTime cdt;
}
