package com.audigo.audigo_back.dto.response.gamification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 운세 정보
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoroscopeInfo {
    private String sign;      // 별자리/띠
    private String contents;  // 운세 내용
}
