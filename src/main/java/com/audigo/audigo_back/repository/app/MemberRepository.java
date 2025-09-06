package com.audigo.audigo_back.repository.app;

import java.math.BigInteger;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.audigo.audigo_back.entity.MemberEntity;

@Repository
public interface MemberRepository extends JpaRepository<MemberEntity, BigInteger>{
    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByMobileNumb(String mobileNumb);

    MemberEntity findByEmail(String email);

    /**
     * 회원가입 
     * DB 함수호출
     * 20 params
     * @param email
     * @param nickname
     * @param birthDt
     * @param gender
     * @param snsDiv
     * @param invitationCd
     * @param inviterCd
     * @param missionYn
     * @param pushTkn
     * @param refreshTkn
     * @param snsId
     * @param snsVal
     * @param model
     * @param appVers
     * @param osVers
     * @param osName
     * @param lang
     * @param mobileNumb
     * @param regionCd
     * @param pushAlive
     * @return
     */
    @Query(value = "SELECT * FROM users.register_member(" +
           ":email, :nickname, :birthDt, :gender, :snsDiv, " +
           ":invitationCd, :inviterCd, :missionYn, :pushTkn, :refreshTkn, " +
           ":snsId, :snsVal, :model, :appVers, :osVers, :osName, " +
           ":lang, :mobileNumb, :regionCd, :pushAlive)", nativeQuery = true)
    Map<String, Object> registerMember(
        @Param("email") String email,
        @Param("nickname") String nickname,
        @Param("birthDt") String birthDt,
        @Param("gender") String gender,
        @Param("snsDiv") String snsDiv,
        @Param("invitationCd") String invitationCd,
        @Param("inviterCd") String inviterCd,
        @Param("missionYn") String missionYn,
        @Param("pushTkn") String pushTkn,
        @Param("refreshTkn") String refreshTkn,
        @Param("snsId") String snsId,
        @Param("snsVal") String snsVal,
        @Param("model") String model,
        @Param("appVers") String appVers,
        @Param("osVers") String osVers,
        @Param("osName") String osName,
        @Param("lang") String lang,
        @Param("mobileNumb") String mobileNumb,
        @Param("regionCd") String regionCd,
        @Param("pushAlive") String pushAlive
    );
}