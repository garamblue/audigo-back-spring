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
     * Find policy by code (any language)
     */
    Optional<RewardPolicyEntity> findFirstByCd(String cd);

    /**
     * Find policies by type
     */
    List<RewardPolicyEntity> findByTpAndLangOrderByCdAsc(String tp, String lang);
}
