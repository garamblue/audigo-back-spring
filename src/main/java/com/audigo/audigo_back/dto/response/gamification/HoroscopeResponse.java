package com.audigo.audigo_back.dto.response.gamification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 운세 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoroscopeResponse {
    private HoroscopeInfo western;      // 서양 별자리 운세
    private HoroscopeInfo eastern;      // 동양 띠 운세
    private String westernEmoji;        // 서양 별자리 이모지
    private String easternEmoji;        // 동양 띠 이모지
    private boolean isComplete;         // 오늘 운세 조회 완료 여부
}
