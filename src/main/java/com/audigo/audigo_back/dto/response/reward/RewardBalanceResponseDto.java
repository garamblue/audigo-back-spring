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
public class RewardBalanceResponseDto {
    private BigDecimal balance;
    private String message;

    public static RewardBalanceResponseDto success(BigDecimal balance) {
        return new RewardBalanceResponseDto(balance, "Success");
    }
}
