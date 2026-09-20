package com.audigo.audigo_back.entity.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 회원 SNS 연동 Entity (users.members_sns)
 */
@Entity
@Table(name = "members_sns", schema = "users")
@Getter
@Setter
public class MembersSnsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "msns_idx")
    private Long msnsIdx;

    @Column(name = "m_idx", nullable = false)
    private BigInteger mIdx;

    @Column(name = "sns_div", length = 10, nullable = false)
    private String snsDiv; // KAKAO, NAVER, GOOGLE, APPLE

    @Column(name = "sns_val", length = 100, nullable = false)
    private String snsVal; // {sns_div}_{sns_id}

    @Column(name = "cdt")
    private LocalDateTime cdt;
}
