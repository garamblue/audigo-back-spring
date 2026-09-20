package com.audigo.audigo_back.entity.member;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;

/**
 * 회원 등급 Entity (fun.members_grade)
 */
@Entity
@Table(name = "members_grade", schema = "fun")
@Getter
@Setter
public class MembersGradeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mg_idx")
    private Long mgIdx;

    @Column(name = "m_idx", nullable = false, unique = true)
    private BigInteger mIdx;

    @Column(name = "skin_size")
    private Integer skinSize = 10; // 기본 스킨 슬롯 수
}
