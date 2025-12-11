package com.audigo.audigo_back.repository.member;

import com.audigo.audigo_back.entity.member.MembersTimezoneEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface MembersTimezoneRepository extends JpaRepository<MembersTimezoneEntity, Long> {

    /**
     * 회원의 활성화된 타임존 조회
     */
    @Query("SELECT mt FROM MembersTimezoneEntity mt " +
           "WHERE mt.mIdx = :mIdx AND mt.stts = 'Y'")
    Optional<MembersTimezoneEntity> findActiveByMIdx(@Param("mIdx") BigInteger mIdx);
}
