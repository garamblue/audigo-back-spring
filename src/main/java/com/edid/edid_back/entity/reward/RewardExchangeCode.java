package com.audigo.audigo_back.entity.reward;

import lombok.Getter;

@Getter
public enum RewardExchangeCode {
    LUCKY("R0000", "Lucky draw"),
    SKIN("R0021", "Exchange for character skin"),
    GIFTISHOW("R0022", "Exchange for gift certificates"),
    SWAP("R0023", "Swap to DIO token"),
    EXPIRED("R0027", "Expired reward (auto-deduction)");

    private final String code;
    private final String description;

    RewardExchangeCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static RewardExchangeCode fromCode(String code) {
        for (RewardExchangeCode exchangeCode : values()) {
            if (exchangeCode.code.equals(code)) {
                return exchangeCode;
            }
        }
        throw new IllegalArgumentException("Unknown reward exchange code: " + code);
    }
}
