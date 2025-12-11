package com.audigo.audigo_back.repository.member;

import com.audigo.audigo_back.entity.member.MembersInvitationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface MembersInvitationRepository extends JpaRepository<MembersInvitationEntity, Long> {

    /**
     * 초대한 사람의 초대 횟수 조회 (최대 30명)
     */
    @Query("SELECT COUNT(mi) FROM MembersInvitationEntity mi WHERE mi.inviterMidx = :inviterMidx")
    Long countByInviterMidx(@Param("inviterMidx") BigInteger inviterMidx);
}
