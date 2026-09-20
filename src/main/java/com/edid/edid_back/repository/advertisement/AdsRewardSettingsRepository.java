package com.audigo.audigo_back.repository.advertisement;

import com.audigo.audigo_back.entity.advertisement.AdsRewardSettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 광고 보상 설정 Repository
 */
@Repository
public interface AdsRewardSettingsRepository extends JpaRepository<AdsRewardSettingsEntity, Long> {

    /**
     * 활성화된 모든 보상 설정 조회
     */
    @Query("SELECT a FROM AdsRewardSettingsEntity a WHERE a.stts = 'Y' ORDER BY a.cdt DESC")
    List<AdsRewardSettingsEntity> findAllActiveSettings();

    /**
     * 특정 모드의 활성화된 보상 설정 조회
     */
    @Query("SELECT a FROM AdsRewardSettingsEntity a WHERE a.mode = :mode AND a.stts = 'Y' ORDER BY a.cdt DESC")
    Optional<AdsRewardSettingsEntity> findActiveByMode(@Param("mode") String mode);

    /**
     * 최신 활성화된 보상 설정 조회 (모드별)
     */
    @Query("SELECT a FROM AdsRewardSettingsEntity a WHERE a.mode = :mode AND a.stts = 'Y' ORDER BY a.cdt DESC LIMIT 1")
    Optional<AdsRewardSettingsEntity> findLatestActiveByMode(@Param("mode") String mode);

    /**
     * 모든 모드별 최신 보상 설정 조회
     */
    @Query(value = "SELECT DISTINCT ON (mode) * FROM ads.ads_reward_settings WHERE stts = 'Y' ORDER BY mode, cdt DESC", nativeQuery = true)
    List<AdsRewardSettingsEntity> findLatestActiveSettingsAllModes();
}
