package com.audigo.audigo_back.repository.scheduler;

import com.audigo.audigo_back.entity.scheduler.ExchangeRatioEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 환율 정보 Repository
 */
@Repository
public interface ExchangeRatioRepository extends JpaRepository<ExchangeRatioEntity, Long> {

    /**
     * 최신 환율 조회
     */
    @Query("SELECT e FROM ExchangeRatioEntity e ORDER BY e.cdt DESC LIMIT 1")
    Optional<ExchangeRatioEntity> findLatest();
}
