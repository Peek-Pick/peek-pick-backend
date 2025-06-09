package org.beep.sbpp.products.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 상품 상세 조회 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDetailDTO {
    private Long productId;
    private String barcode;
    private String name;
    private String description;
    private String category;
    private String volume;
    private String imgUrl;
    private String ingredients;
    private String allergens;
    private String nutrition;
    private Integer likeCount;
    private Integer reviewCount;
    private BigDecimal score;
    @Builder.Default
    private Boolean isLiked=false;
}