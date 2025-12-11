package com.audigo.audigo_back.repository.terms;

import com.audigo.audigo_back.entity.terms.TermsConditionsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TermsConditionsRepository extends JpaRepository<TermsConditionsEntity, Long> {

    /**
     * 최신 약관 조회 (타입 및 지역별)
     */
    @Query("SELECT tc FROM TermsConditionsEntity tc " +
           "WHERE tc.tp = :tp " +
           "AND tc.regionCd = :regionCd " +
           "AND (tc.applyDt < CURRENT_TIMESTAMP OR tc.applyDt IS NULL) " +
           "ORDER BY tc.tcVers DESC " +
           "LIMIT 1")
    Optional<TermsConditionsEntity> findLatestByTypeAndRegion(@Param("tp") String tp,
                                                                @Param("regionCd") String regionCd);
}
