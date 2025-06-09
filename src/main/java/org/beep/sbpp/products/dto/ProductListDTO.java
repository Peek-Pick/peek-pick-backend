package org.beep.sbpp.products.dto;

import lombok.*;
import org.beep.sbpp.products.entities.ProductEntity;

import java.math.BigDecimal;

/**
 * 상품 목록 조회 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListDTO {
    private Long productId;
    private String barcode;
    private String name;
    private String category;
    private String imgUrl;
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
    public static ProductListDTO fromEntity(ProductEntity e) {
        return ProductListDTO.builder()
                .productId(e.getProductId())
                .barcode(e.getBarcode())
                .name(e.getName())
                .category(e.getCategory())
                .imgUrl(e.getImgUrl())
                .likeCount(e.getLikeCount())
                .reviewCount(e.getReviewCount())
                .score(e.getScore())
                .isLiked(false)
                .isDelete(e.getIsDelete())
                .build();
    }
}
