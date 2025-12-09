package com.audigo.audigo_back.entity.reward;

import lombok.Getter;

@Getter
public enum RewardTableCode {
    ADS_RESPONSE("C001001", "Audio Ad Response"),
    VIDEO_ADS_HIS("C001002", "Video Ad History"),
    GIFTISHOW("C001005", "GiftiShow Exchange"),
    PINCRUX_OFFER("C001006", "Pincrux Offer"),
    REWARD_ADJUST("C001007", "Reward Adjustment"),
    ATTENDANCE("C001008", "Daily Attendance"),
    ROULETTE_COUPON("C001009", "Roulette Coupon"),
    ROULETTE_HIS("C001010", "Roulette History"),
    RANKING("C001011", "Monthly Ranking"),
    COMPLETE("C001012", "Complete Mission");

    private final String code;
    private final String description;

    RewardTableCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static RewardTableCode fromCode(String code) {
        for (RewardTableCode tableCode : values()) {
            if (tableCode.code.equals(code)) {
                return tableCode;
            }
        }
        throw new IllegalArgumentException("Unknown reward table code: " + code);
    }
}
