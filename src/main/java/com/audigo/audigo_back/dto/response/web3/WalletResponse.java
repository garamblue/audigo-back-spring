package com.audigo.audigo_back.dto.response.web3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalletResponse {
    private Long ewIdx;
    private String address;
    private BigDecimal tokenBalance;
    private BigDecimal bnbBalance;
    private String txHash;  // 등록 시 지급 트랜잭션
}
