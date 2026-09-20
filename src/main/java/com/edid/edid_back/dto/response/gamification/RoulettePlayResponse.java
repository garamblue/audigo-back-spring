package com.audigo.audigo_back.dto.response.gamification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 룰렛 실행 결과 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoulettePlayResponse {
    private List<RouletteRewardInfo> policy; // 룰렛 보상 정책
    private RouletteRewardInfo result;       // 당첨 결과
}
