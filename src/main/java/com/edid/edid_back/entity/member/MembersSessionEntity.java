package com.audigo.audigo_back.entity.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 회원 세션 Entity (users.members_session)
 */
@Entity
@Table(name = "members_session", schema = "users")
@Getter
@Setter
public class MembersSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "msl_idx")
    private Long mslIdx;

    @Column(name = "m_idx", nullable = false)
    private BigInteger mIdx;

    @Column(name = "refresh_tkn", length = 500, nullable = false)
    private String refreshTkn;

    @Column(name = "device_id", length = 100, nullable = false)
    private String deviceId;

    @Column(name = "push_tkn", length = 500)
    private String pushTkn;

    @Column(name = "token_exdt")
    private LocalDateTime tokenExdt; // 토큰 만료일시

    @Column(name = "expired", length = 1)
    private String expired = "N"; // Y=만료, N=유효

    @Column(name = "cdt")
    private LocalDateTime cdt;
}
