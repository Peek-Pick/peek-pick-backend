package org.beep.sbpp.products.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 상품 상세 조회 응답 DTO
 */
@AllArgsConstructor
@Getter
public class ProductDetailDTO {
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
}