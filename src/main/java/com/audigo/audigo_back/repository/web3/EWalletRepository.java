package com.audigo.audigo_back.repository.web3;

import com.audigo.audigo_back.entity.web3.EWalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface EWalletRepository extends JpaRepository<EWalletEntity, Long> {

    /**
     * 회원 ID로 지갑 조회
     */
    Optional<EWalletEntity> findByMIdx(BigInteger mIdx);

    /**
     * 지갑 주소로 조회
     */
    Optional<EWalletEntity> findByAddr(String addr);

    /**
     * 회원이 지갑을 등록했는지 확인 (주소가 비어있지 않은지)
     */
    @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EWalletEntity e " +
           "WHERE e.mIdx = :mIdx AND e.addr IS NOT NULL AND e.addr != ''")
    boolean hasWalletAddress(@Param("mIdx") BigInteger mIdx);
}
