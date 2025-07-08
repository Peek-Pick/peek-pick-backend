package org.beep.sbpp.products.dto;

import lombok.*;
import org.beep.sbpp.products.entities.*;
import org.beep.sbpp.search.document.ProductSearchDocument;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 상품 목록 조회 응답 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ProductListDTO {
    private Long productId;
    private String barcode;
    private String name;
    private String category;
    private String imgThumbUrl;
    private Integer likeCount;
    private Integer reviewCount;
    private BigDecimal score;

    @Builder.Default
    private Boolean isLiked = false;

    /** soft-delete 여부 */
    private Boolean isDelete;

    /** 찜 목록 전용: 수정일 (ProductLikeEntity 기준, 커서 페이징용) */
    private LocalDateTime modDate;




    /**
     * Elasticsearch 문서 기반 검색 결과 → DTO 변환 (_score 없이)
     */
    public static ProductListDTO fromSearchDocument(ProductSearchDocument d) {
        return ProductListDTO.builder()
                .productId(Long.parseLong(d.getId()))
                .barcode(d.getBarcode())
                .name(d.getName())
                .category(d.getCategory())
                .imgThumbUrl(d.getImgThumbUrl())
                .likeCount(d.getLikeCount())
                .reviewCount(d.getReviewCount())
                .score(d.getScore())
                .isLiked(false)
                .isDelete(d.getIsDelete())
                .build();
    }

/// ////////////////////////////////////////////////

    /** Base + Lang 인터페이스만 보고 생성 */
    public static ProductListDTO fromEntities(ProductBaseEntity base, ProductLangEntity lang) {
        return ProductListDTO.builder()
                .productId(base.getProductId())
                .barcode(base.getBarcode())
                .name(lang.getName())
                .category(lang.getCategory())
                .imgThumbUrl(base.getImgThumbUrl())
                .likeCount(base.getLikeCount())
                .reviewCount(base.getReviewCount())
                .score(base.getScore())
                .isLiked(false)
                .isDelete(base.getIsDelete())
                .build();
    }

    /** 찜 목록용 (modDate 추가) */
    public static ProductListDTO fromEntitiesWithModDate(ProductBaseEntity base, ProductLangEntity lang, LocalDateTime modDate) {
        return fromEntities(base, lang).toBuilder()
                .modDate(modDate)
                .build();
    }

}
