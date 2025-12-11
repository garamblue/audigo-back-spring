package com.audigo.audigo_back.repository.skin;

import com.audigo.audigo_back.entity.skin.SkinExchangeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface SkinExchangeRepository extends JpaRepository<SkinExchangeEntity, Long> {

    /**
     * 회원의 스킨 목록 조회 (최신순)
     */
    List<SkinExchangeEntity> findByMIdxOrderByCdtDesc(BigInteger mIdx);
}
