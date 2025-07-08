package org.beep.sbpp.products.dto;

import lombok.*;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.products.entities.ProductLangEntity;

import java.math.BigDecimal;

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
    private Boolean isLiked = false;
    private Boolean isDelete;

    /**
     * ProductBaseEntity + ProductLangEntity 인터페이스만 보고 DTO 생성.
     * 언어별 분기 전혀 없음.
     */
    public static ProductDetailDTO fromEntities(ProductBaseEntity base, ProductLangEntity lang) {
        return ProductDetailDTO.builder()
                .productId(base.getProductId())
                .barcode(base.getBarcode())
                .name(lang.getName())
                .description(lang.getDescription())
                .category(lang.getCategory())
                .volume(lang.getVolume())
                .imgUrl(base.getImgUrl())
                .ingredients(lang.getIngredients())
                .allergens(lang.getAllergens())
                .nutrition(lang.getNutrition())
                .likeCount(base.getLikeCount())
                .reviewCount(base.getReviewCount())
                .score(base.getScore())
                .isLiked(false)
                .isDelete(base.getIsDelete())
                .build();
    }
}
