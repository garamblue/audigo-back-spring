package com.audigo.audigo_back.dto.request.market;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeRequestDto {

    @NotNull(message = "Product index is required")
    private BigInteger gpIdx;

    @NotNull(message = "Goods code is required")
    private String goodsCode;

    @NotNull(message = "Mobile number is required")
    @Pattern(regexp = "^\\d{10,11}$", message = "Invalid mobile number format")
    private String mobileNum;
}
