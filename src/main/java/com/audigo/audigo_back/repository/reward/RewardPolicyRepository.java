package com.audigo.audigo_back.repository.reward;

import com.audigo.audigo_back.entity.reward.RewardPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

@Repository
public interface RewardPolicyRepository extends JpaRepository<RewardPolicyEntity, BigInteger> {

    /**
     * Find policy by code and language
     */
    Optional<RewardPolicyEntity> findByCdAndLang(String cd, String lang);

    /**
     * Find all policies by language
     */
    List<RewardPolicyEntity> findByLangOrderByCdAsc(String lang);

    /**
     * Find active policies by language
     */
    @Query("SELECT rp FROM RewardPolicyEntity rp WHERE rp.lang = :lang AND rp.stts = 'Y' ORDER BY rp.cd ASC")
    List<RewardPolicyEntity> findActiveByLang(@Param("lang") String lang);

    /**
     * 코드로 보상 정책 조회
     */
    @Query("SELECT r FROM RewardPolicyEntity r WHERE r.cd = :cd AND r.stts = 'Y'")
    Optional<RewardPolicyEntity> findActiveByCd(@Param("cd") String cd);

    /**
     * Find policy by code (any language)
     */
    Optional<RewardPolicyEntity> findFirstByCd(String cd);

    /**
     * Find policies by type
     */
    List<RewardPolicyEntity> findByTpAndLangOrderByCdAsc(String tp, String lang);

    /**
     * 룰렛 보상 정책 조회 (언어별)
     */
    @Query("SELECT r FROM RewardPolicyEntity r WHERE r.cd LIKE 'RL%' AND r.stts = 'Y' AND r.lang = :lang ORDER BY r.rAmt DESC")
    List<RewardPolicyEntity> findRouletteRewardsByLang(@Param("lang") String lang);

    /**
     * 활성화된 모든 보상 정책 조회
     */
    @Query("SELECT r FROM RewardPolicyEntity r WHERE r.stts = 'Y' ORDER BY r.cd")
    List<RewardPolicyEntity> findAllActive();
}
