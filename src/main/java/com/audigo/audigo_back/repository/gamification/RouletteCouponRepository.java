package com.audigo.audigo_back.repository.gamification;

import com.audigo.audigo_back.entity.gamification.RouletteCouponEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

/**
 * 룰렛 쿠폰 Repository
 */
@Repository
public interface RouletteCouponRepository extends JpaRepository<RouletteCouponEntity, Long> {

    /**
     * 특정 회원의 쿠폰 조회
     */
    @Query("SELECT r FROM RouletteCouponEntity r WHERE r.mIdx = :mIdx")
    Optional<RouletteCouponEntity> findByMIdx(@Param("mIdx") BigInteger mIdx);

    /**
     * 쿠폰 사용 (차감)
     */
    @Modifying
    @Query("UPDATE RouletteCouponEntity r SET r.cnt = r.cnt - 1, r.udt = CURRENT_TIMESTAMP WHERE r.mIdx = :mIdx AND r.cnt > 0")
    int decrementCoupon(@Param("mIdx") BigInteger mIdx);

    /**
     * 쿠폰 추가
     */
    @Modifying
    @Query("UPDATE RouletteCouponEntity r SET r.cnt = r.cnt + :amount, r.udt = CURRENT_TIMESTAMP WHERE r.mIdx = :mIdx")
    int incrementCoupon(@Param("mIdx") BigInteger mIdx, @Param("amount") int amount);
}
