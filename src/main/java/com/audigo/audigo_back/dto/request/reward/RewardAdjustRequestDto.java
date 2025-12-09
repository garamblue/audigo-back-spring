package com.audigo.audigo_back.dto.request.reward;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RewardAdjustRequestDto {

    @NotNull(message = "Member ID is required")
    private BigInteger mIdx;

    @NotNull(message = "Reward code is required")
    private String code;

    @NotNull(message = "Type is required")
    @Pattern(regexp = "^[PME]$", message = "Type must be P (Plus), M (Minus), or E (Expired)")
    private String type;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private LocalDateTime scheduledDate;  // null = immediate, future = scheduled
}
