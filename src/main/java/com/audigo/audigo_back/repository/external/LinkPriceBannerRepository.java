package com.audigo.audigo_back.repository.external;

import com.audigo.audigo_back.entity.external.LinkPriceBannerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * LinkPrice 배너 Repository
 */
@Repository
public interface LinkPriceBannerRepository extends JpaRepository<LinkPriceBannerEntity, Long> {

    /**
     * 활성화된 배너 조회 (랜덤 3개)
     */
    @Query(value = "SELECT * FROM external.linkprice_banner WHERE stts = 'Y' ORDER BY RANDOM() LIMIT 3", nativeQuery = true)
    List<LinkPriceBannerEntity> findRandomThree();

    /**
     * 활성화된 모든 배너 조회
     */
    @Query("SELECT l FROM LinkPriceBannerEntity l WHERE l.stts = 'Y' ORDER BY l.cdt DESC")
    List<LinkPriceBannerEntity> findAllActive();
}
