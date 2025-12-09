package com.audigo.audigo_back.dto.response.reward;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RewardExpirationResponseDto {
    private BigDecimal expiringAmount;
    private String message;

    public static RewardExpirationResponseDto of(BigDecimal amount) {
        return new RewardExpirationResponseDto(
            amount,
            amount.compareTo(BigDecimal.ZERO) > 0
                ? "Rewards expiring in 1 month: " + amount
                : "No rewards expiring soon"
        );
    }
}
