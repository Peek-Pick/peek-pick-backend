package org.beep.sbpp.products.dto;

import lombok.*;
import org.beep.sbpp.products.entities.ProductEntity;

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
    private Boolean isLiked = false;
    /** soft-delete 여부 */
    private Boolean isDelete;

    /**
     * Entity → DTO 변환 메서드
     */
    public static ProductDetailDTO fromEntity(ProductEntity e) {
        return ProductDetailDTO.builder()
                .productId(e.getProductId())
                .barcode(e.getBarcode())
                .name(e.getName())
                .description(e.getDescription())
                .category(e.getCategory())
                .volume(e.getVolume())
                .imgUrl(e.getImgUrl())
                .ingredients(e.getIngredients())
                .allergens(e.getAllergens())
                .nutrition(e.getNutrition())
                .likeCount(e.getLikeCount())
                .reviewCount(e.getReviewCount())
                .score(e.getScore())
                .isLiked(false)
                .isDelete(e.getIsDelete())
                .build();
    }
}
