// src/main/java/org/beep/sbpp/search/document/ProductSearchDocument.java
package org.beep.sbpp.search.document;

import lombok.*;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.products.entities.ProductLangEntity;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSearchDocument {
    /** ES 문서 ID: ProductBaseEntity.productId 문자열 */
    private String id;

    private Long productId;
    private String name;
    private String description;
    private String barcode;
    private String category;
    private String volume;
    private String ingredients;
    private String allergens;
    private String nutrition;
    private String imgThumbUrl;
    private Boolean isDelete;
    private Integer likeCount;
    private Integer reviewCount;
    private BigDecimal score;
    private String mainTag;

    /**
     * Base + Lang → ES 문서 변환
     */
    public static ProductSearchDocument fromEntities(
            ProductBaseEntity base, ProductLangEntity lang
    ) {
        return ProductSearchDocument.builder()
                .id(String.valueOf(base.getProductId()))
                .productId(base.getProductId())
                .name(lang.getName())
                .description(lang.getDescription())
                .barcode(base.getBarcode())
                .category(lang.getCategory())
                .volume(lang.getVolume())
                .ingredients(lang.getIngredients())
                .allergens(lang.getAllergens())
                .nutrition(lang.getNutrition())
                .imgThumbUrl(base.getImgThumbUrl())
                .isDelete(base.getIsDelete())
                .likeCount(base.getLikeCount())
                .reviewCount(base.getReviewCount())
                .score(base.getScore())
                .mainTag(base.getMainTag())
                .build();
    }
}
