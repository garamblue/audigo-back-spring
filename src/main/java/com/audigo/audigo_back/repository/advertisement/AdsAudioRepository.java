package com.audigo.audigo_back.repository.advertisement;

import com.audigo.audigo_back.entity.advertisement.AdsAudioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 오디오 광고 Repository
 */
@Repository
public interface AdsAudioRepository extends JpaRepository<AdsAudioEntity, Long> {

    /**
     * 활성화된 광고 조회 (타입별)
     */
    @Query("SELECT a FROM AdsAudioEntity a WHERE a.stts = 'Y' AND a.tp = :tp ORDER BY a.cdt DESC")
    List<AdsAudioEntity> findActiveAdsByType(@Param("tp") String tp);

    /**
     * 활성화된 모든 광고 조회
     */
    @Query("SELECT a FROM AdsAudioEntity a WHERE a.stts = 'Y' ORDER BY a.cdt DESC")
    List<AdsAudioEntity> findAllActiveAds();

    /**
     * 특정 광고 ID로 활성화된 광고 조회
     */
    @Query("SELECT a FROM AdsAudioEntity a WHERE a.aaIdx = :aaIdx AND a.stts = 'Y'")
    Optional<AdsAudioEntity> findActiveAdById(@Param("aaIdx") Long aaIdx);

    /**
     * 특정 모드의 활성화된 광고 조회
     */
    @Query("SELECT a FROM AdsAudioEntity a WHERE a.stts = 'Y' AND a.mode = :mode ORDER BY a.cdt DESC")
    List<AdsAudioEntity> findActiveAdsByMode(@Param("mode") String mode);

    /**
     * 키워드로 광고 검색
     */
    @Query("SELECT a FROM AdsAudioEntity a WHERE a.stts = 'Y' AND a.keyword LIKE %:keyword% ORDER BY a.cdt DESC")
    List<AdsAudioEntity> findActiveAdsByKeyword(@Param("keyword") String keyword);
}
