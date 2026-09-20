package com.audigo.audigo_back.entity;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "refresh_token")
@Table(name = "refresh_token")
public class RefreshTokenEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rt_idx")
    private Long rtIdx;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "refresh_token", nullable = false, length = 500)
    private String refreshToken;
    
    @Column(name = "expires_at", nullable = false)
    private Timestamp expiresAt;
    
    @Column(name = "created_at", nullable = false)
    private Timestamp createdAt;
    
    public RefreshTokenEntity(String userId, String refreshToken, Timestamp expiresAt) {
        this.userId = userId;
        this.refreshToken = refreshToken;
        this.expiresAt = expiresAt;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }
    
    public boolean isExpired() {
        return expiresAt.before(new Timestamp(System.currentTimeMillis()));
    }
}