package com.audigo.audigo_back.dto.response.external;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * LinkPrice 배너 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LinkPriceBannerResponse {
    private Long lbIdx;
    private String productName;
    private String productUrl;
    private String imageUrl;
    private Integer price;
    private String category;
}
