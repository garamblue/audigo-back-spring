package com.audigo.audigo_back.dto.request.reward;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RewardTopupRequestDto {

    @NotNull(message = "Member ID is required")
    private BigInteger mIdx;

    @NotNull(message = "Reward code is required")
    private String code;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private BigInteger sourceTableIdx;  // Optional reference to source
    private String sourceTableName;      // Optional source table code
}
