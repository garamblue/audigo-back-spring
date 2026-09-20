package com.audigo.audigo_back.entity.gamification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;
import java.time.LocalDateTime;

/**
 * 출석 체크
 * 테이블: fun.daily_attendance_check
 */
@Entity
@Table(name = "daily_attendance_check", schema = "fun")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dac_idx")
    private Long dacIdx;

    @Column(name = "m_idx", nullable = false)
    private BigInteger mIdx;  // 회원 ID

    @Column(name = "cdt", nullable = false)
    private LocalDateTime cdt;

    @PrePersist
    protected void onCreate() {
        cdt = LocalDateTime.now();
    }
}
