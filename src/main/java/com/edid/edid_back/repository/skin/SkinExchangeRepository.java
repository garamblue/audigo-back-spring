package com.edid.edid_back.repository.skin;

import com.edid.edid_back.entity.skin.SkinExchangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface SkinExchangeRepository extends JpaRepository<SkinExchangeEntity, Long> {

    /**
     * 회원의 스킨 목록 조회 (최신순)
     */
    @Query("SELECT s FROM SkinExchangeEntity s WHERE s.mIdx = :mIdx ORDER BY s.cdt DESC")
    List<SkinExchangeEntity> findByMIdxOrderByCdtDesc(@Param("mIdx") BigInteger mIdx);
}
