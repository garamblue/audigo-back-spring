package com.audigo.audigo_back.entity.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 회원 초대 관계 Entity (users.members_invitation)
 */
@Entity
@Table(name = "members_invitation", schema = "users")
@Getter
@Setter
public class MembersInvitationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mi_idx")
    private Long miIdx;

    @Column(name = "inviter_midx", nullable = false)
    private BigInteger inviterMidx; // 초대한 사람

    @Column(name = "m_idx", nullable = false)
    private BigInteger mIdx; // 초대받은 사람

    @Column(name = "cdt")
    private LocalDateTime cdt;
}
