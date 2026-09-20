package com.audigo.audigo_back.dto.response.web3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private String txHash;
    private String status;  // pending, success, failed
    private String fromAddress;
    private String toAddress;
    private BigDecimal amount;
    private BigDecimal gasUsed;
    private Long blockNumber;
    private LocalDateTime timestamp;
}
