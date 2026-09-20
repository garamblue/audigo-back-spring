package com.audigo.audigo_back.repository.auth;

import com.audigo.audigo_back.entity.auth.MobileVerifyHisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MobileVerifyHisRepository extends JpaRepository<MobileVerifyHisEntity, Long> {

    /**
     * 오늘 요청 횟수 조회 (하루 3회 제한)
     */
    @Query("SELECT COUNT(m) FROM MobileVerifyHisEntity m " +
           "WHERE m.mobileNum = :mobileNum " +
           "AND CAST(m.cdt AS date) = CAST(CURRENT_TIMESTAMP AS date)")
    Long countTodayRequests(@Param("mobileNum") String mobileNum);

    /**
     * 인증 코드 확인 (만료되지 않은 유효한 코드)
     */
    @Query("SELECT m FROM MobileVerifyHisEntity m " +
           "WHERE m.mobileNum = :mobileNum " +
           "AND m.authCd = :authCd " +
           "AND m.expYn = 'N' " +
           "AND m.expDt > CURRENT_TIMESTAMP")
    Optional<MobileVerifyHisEntity> findValidAuthCode(@Param("mobileNum") String mobileNum,
                                                        @Param("authCd") String authCd);
}
