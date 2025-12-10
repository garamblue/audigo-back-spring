package com.audigo.audigo_back.repository.gamification;

import com.audigo.audigo_back.entity.gamification.RewardPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 보상 정책 Repository
 */
@Repository
public interface RewardPolicyRepository extends JpaRepository<RewardPolicyEntity, Long> {

    /**
     * 코드로 보상 정책 조회
     */
    @Query("SELECT r FROM RewardPolicyEntity r WHERE r.cd = :cd AND r.stts = 'Y'")
    Optional<RewardPolicyEntity> findActiveByCd(@Param("cd") String cd);

    /**
     * 룰렛 보상 정책 조회 (언어별)
     */
    @Query("SELECT r FROM RewardPolicyEntity r WHERE r.cdTp = 'RL' AND r.stts = 'Y' AND r.lang = :lang ORDER BY r.rAmt DESC")
    List<RewardPolicyEntity> findRouletteRewardsByLang(@Param("lang") String lang);

    /**
     * 활성화된 모든 보상 정책 조회
     */
    @Query("SELECT r FROM RewardPolicyEntity r WHERE r.stts = 'Y' ORDER BY r.cd")
    List<RewardPolicyEntity> findAllActive();
}
