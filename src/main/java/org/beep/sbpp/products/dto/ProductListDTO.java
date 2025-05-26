package org.beep.sbpp.products.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 상품 랭킹 목록 조회용 DTO
 */
@AllArgsConstructor
@Getter
public class ProductListDTO {
    private Long productId;
    private String name;
    private String category;
    private String imgUrl;
    private Integer likeCount;
}
