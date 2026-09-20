package com.audigo.audigo_back.repository.external;

import com.audigo.audigo_back.entity.external.LinkPriceBannerHisEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;

/**
 * LinkPrice 배너 클릭 히스토리 Repository
 */
@Repository
public interface LinkPriceBannerHisRepository extends JpaRepository<LinkPriceBannerHisEntity, Long> {

    /**
     * 특정 회원의 배너 클릭 이력 조회
     */
    @Query("SELECT l FROM LinkPriceBannerHisEntity l WHERE l.mIdx = :mIdx ORDER BY l.cdt DESC")
    List<LinkPriceBannerHisEntity> findByMIdx(@Param("mIdx") BigInteger mIdx);

    /**
     * 특정 시간 범위 내 회원의 배너 클릭 수 카운트 (일일 제한 체크용)
     */
    @Query("SELECT COUNT(l) FROM LinkPriceBannerHisEntity l WHERE l.mIdx = :mIdx AND l.cdt >= :startTime AND l.cdt < :endTime")
    Long countByMIdxAndDateRange(
            @Param("mIdx") BigInteger mIdx,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}
