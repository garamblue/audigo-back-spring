package com.audigo.audigo_back.repository.member;

import com.audigo.audigo_back.entity.member.MembersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MembersRepository extends JpaRepository<MembersEntity, BigInteger> {

    /**
     * 초대 코드로 회원 조회
     */
    @Query("SELECT m FROM MembersEntity m " +
           "WHERE m.invitCd = :invitCd " +
           "AND (m.stts = '1' OR m.stts = '4') " +
           "AND m.lvDt IS NULL")
    Optional<MembersEntity> findByInvitCd(@Param("invitCd") String invitCd);

    /**
     * 외부 키로 회원 조회
     */
    Optional<MembersEntity> findByExtKey(String extKey);

    /**
     * 이메일과 휴대폰번호/생년월일로 회원 조회
     */
    @Query("SELECT m FROM MembersEntity m " +
           "WHERE m.email = :email " +
           "AND (m.mobileNum = :mobileNum OR m.birthDt = :birthDt)")
    List<MembersEntity> findByEmailAndMobileOrBirth(@Param("email") String email,
                                                      @Param("mobileNum") String mobileNum,
                                                      @Param("birthDt") LocalDate birthDt);
}
