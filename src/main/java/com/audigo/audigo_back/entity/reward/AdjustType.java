package com.audigo.audigo_back.entity.reward;

import lombok.Getter;

@Getter
public enum AdjustType {
    P("Plus/Topup - Add rewards"),
    M("Minus/Exchange - Deduct rewards"),
    E("Expired - Auto-deduction for expired rewards");

    private final String description;

    AdjustType(String description) {
        this.description = description;
    }

    public static AdjustType fromString(String type) {
        for (AdjustType adjustType : values()) {
            if (adjustType.name().equals(type)) {
                return adjustType;
            }
        }
        throw new IllegalArgumentException("Unknown adjust type: " + type);
    }
}
