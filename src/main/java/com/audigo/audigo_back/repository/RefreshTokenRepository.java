package com.audigo.audigo_back.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.audigo.audigo_back.entity.RefreshTokenEntity;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    
    Optional<RefreshTokenEntity> findByRefreshToken(String refreshToken);
    
    Optional<RefreshTokenEntity> findByUserId(String userId);
    
    void deleteByUserId(String userId);
    
    void deleteByRefreshToken(String refreshToken);
}