package com.audigo.audigo_back.repository.member;

import com.audigo.audigo_back.entity.member.MembersSessionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface MembersSessionRepository extends JpaRepository<MembersSessionEntity, Long> {

    /**
     * 유효한 세션 확인 (토큰 검증용)
     */
    @Query("SELECT ms FROM MembersSessionEntity ms " +
           "WHERE ms.pushTkn = :pushTkn " +
           "AND ms.tokenExdt > CURRENT_TIMESTAMP " +
           "AND ms.expired = 'N'")
    Optional<MembersSessionEntity> findValidSession(@Param("pushTkn") String pushTkn);

    /**
     * 기존 세션 조회 (device_id + push_tkn)
     */
    @Query("SELECT ms FROM MembersSessionEntity ms " +
           "WHERE ms.mIdx = :mIdx " +
           "AND ms.deviceId = :deviceId " +
           "AND ms.tokenExdt > CURRENT_TIMESTAMP " +
           "AND ms.pushTkn = :pushTkn")
    Optional<MembersSessionEntity> findByDeviceAndToken(@Param("mIdx") BigInteger mIdx,
                                                          @Param("deviceId") String deviceId,
                                                          @Param("pushTkn") String pushTkn);

    /**
     * 회원의 모든 세션 만료 처리
     */
    @Modifying
    @Query("UPDATE MembersSessionEntity ms " +
           "SET ms.expired = 'Y', ms.tokenExdt = CURRENT_TIMESTAMP " +
           "WHERE ms.mIdx = :mIdx")
    void expireAllSessions(@Param("mIdx") BigInteger mIdx);
}
