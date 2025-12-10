package com.audigo.audigo_back.dto.response.gamification;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 룰렛 상태 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouletteStatusResponse {
    private int cnt;                        // 보유 쿠폰 수
    private List<RouletteWinnerInfo> list;  // 최근 당첨자 목록
    private List<RouletteRewardInfo> policy; // 룰렛 보상 정책
    private String avail;                   // Y=사용가능, N=대기중
    private String title;                   // 안내 제목 (대기중일 때)
    private String body;                    // 안내 내용 (대기중일 때)
    private String info;                    // 안내 정보 (대기중일 때)
}
