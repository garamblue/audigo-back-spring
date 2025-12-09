package com.audigo.audigo_back.entity.reward;

import lombok.Getter;

@Getter
public enum RewardTopupCode {
    INVITER("R0001", "Referral - Inviter bonus"),
    INVITEE("R0002", "Referral - Invitee bonus"),
    ATTENDANCE("R0004", "Daily attendance check"),
    ATTENDANCE_7("R0005", "7-day consecutive attendance"),
    ROULETTE_1000("R0006", "Roulette - 1000 points"),
    ROULETTE_500("R0007", "Roulette - 500 points"),
    ROULETTE_100("R0008", "Roulette - 100 points"),
    ROULETTE_50("R0009", "Roulette - 50 points"),
    ROULETTE_20("R0010", "Roulette - 20 points"),
    ROULETTE_10("R0011", "Roulette - 10 points"),
    ROULETTE_5("R0012", "Roulette - 5 points"),
    ROULETTE_0("R0013", "Roulette - 0 points"),
    INTERVAL("R0014", "Interval listening reward"),
    CONTINUOUS("R0015", "Continuous listening reward"),
    HOROSCOPE("R0016", "Horoscope daily reward"),
    COMPLETE("R0017", "Complete mission reward"),
    PINCRUX("R0028", "Pincrux offer reward"),
    RANKING("R0029", "Monthly ranking reward"),
    BANNER("R0030", "Banner ad reward");

    private final String code;
    private final String description;

    RewardTopupCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static RewardTopupCode fromCode(String code) {
        for (RewardTopupCode topupCode : values()) {
            if (topupCode.code.equals(code)) {
                return topupCode;
            }
        }
        throw new IllegalArgumentException("Unknown reward topup code: " + code);
    }
}
