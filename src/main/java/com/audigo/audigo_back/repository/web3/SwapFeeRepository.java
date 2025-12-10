package com.audigo.audigo_back.repository.web3;

import com.audigo.audigo_back.entity.web3.SwapFeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SwapFeeRepository extends JpaRepository<SwapFeeEntity, Long> {

    /**
     * 최신 스왑 수수료 조회
     */
    @Query("SELECT s FROM SwapFeeEntity s ORDER BY s.rcrIdx DESC LIMIT 1")
    Optional<SwapFeeEntity> findLatest();
}
