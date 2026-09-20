package com.audigo.audigo_back.dto.response.gamification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 출석 체크 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttendanceResponse {
    private int count;           // 연속 출석 일수
    private boolean isCompleted; // 오늘 출석 완료 여부
}
