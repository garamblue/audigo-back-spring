package com.audigo.audigo_back.dto.response.reward;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RewardHistoryResponseDto {
    private BigDecimal currentBalance;
    private List<RewardTransactionDto> transactions;
    private int totalPages;
    private long totalElements;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RewardTransactionDto {
        private String code;
        private String description;
        private BigDecimal amount;
        private LocalDateTime transactionDate;
        private String type; // "topup" or "exchange"
    }
}
