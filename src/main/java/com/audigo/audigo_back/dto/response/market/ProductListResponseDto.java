package com.audigo.audigo_back.dto.response.market;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductListResponseDto {
    private List<ProductDto> products;
    private int totalPages;
    private long totalElements;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductDto {
        private BigInteger gpIdx;
        private String goodsCode;
        private String goodsName;
        private String brandCode;
        private BigDecimal realPrice;
        private BigDecimal appPrice;
        private String goodsImgs;
        private String goodsImgb;
        private String limitDay;
        private String validPrdDay;
    }
}
