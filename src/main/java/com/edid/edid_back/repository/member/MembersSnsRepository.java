package com.audigo.audigo_back.repository.member;

import com.audigo.audigo_back.entity.member.MembersSnsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembersSnsRepository extends JpaRepository<MembersSnsEntity, Long> {

    /**
     * SNS 계정으로 회원 조회 (탈퇴하지 않은 회원)
     */
    @Query("SELECT ms FROM MembersSnsEntity ms " +
           "INNER JOIN MembersEntity m ON ms.mIdx = m.mIdx " +
           "WHERE ms.snsDiv = :snsDiv " +
           "AND ms.snsVal = :snsVal " +
           "AND (m.stts = '1' OR m.stts = '4') " +
           "AND m.lvDt IS NULL " +
           "ORDER BY m.mIdx DESC")
    List<MembersSnsEntity> findBySnsAccount(@Param("snsDiv") String snsDiv,
                                             @Param("snsVal") String snsVal);

    /**
     * 회원의 모든 SNS 계정 조회
     */
    @Query("SELECT ms FROM MembersSnsEntity ms WHERE ms.mIdx = :mIdx")
    List<MembersSnsEntity> findByMIdx(@Param("mIdx") BigInteger mIdx);

    /**
     * 회원의 SNS 계정 문자열로 반환
     */
    @Query("SELECT STRING_AGG(ms.snsDiv, ', ') FROM MembersSnsEntity ms WHERE ms.mIdx = :mIdx")
    Optional<String> findSnsListByMIdx(@Param("mIdx") BigInteger mIdx);
}
