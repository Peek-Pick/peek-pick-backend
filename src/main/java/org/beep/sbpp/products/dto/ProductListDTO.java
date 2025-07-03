package org.beep.sbpp.products.dto;

import lombok.*;
import org.beep.sbpp.products.entities.ProductEntity;
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
     * 일반 상품 조회용: ProductEntity → DTO 변환 메서드
     */
    public static ProductListDTO fromEntity(ProductEntity e) {
        return ProductListDTO.builder()
                .productId(e.getProductId())
                .barcode(e.getBarcode())
                .name(e.getName())
                .category(e.getCategory())
                .imgThumbUrl(e.getImgThumbUrl())
                .likeCount(e.getLikeCount())
                .reviewCount(e.getReviewCount())
                .score(e.getScore())
                .isLiked(false)
                .isDelete(e.getIsDelete())
                .build();
    }

    /**
     * 찜한 상품 목록용: ProductEntity + modDate 수동 지정
     */
    public static ProductListDTO fromEntityWithModDate(ProductEntity e, LocalDateTime modDate) {
        return ProductListDTO.builder()
                .productId(e.getProductId())
                .barcode(e.getBarcode())
                .name(e.getName())
                .category(e.getCategory())
                .imgThumbUrl(e.getImgThumbUrl())
                .likeCount(e.getLikeCount())
                .reviewCount(e.getReviewCount())
                .score(e.getScore())
                .isLiked(false)
                .isDelete(e.getIsDelete())
                .modDate(modDate)
                .build();
    }

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

    /**
     * Elasticsearch 검색 결과 → DTO 변환 (정확도 _score 포함)
     */
    public static ProductListDTO fromSearchDocumentWithScore(ProductSearchDocument d, float elasticScore) {
        return fromSearchDocument(d).toBuilder()
               .build();
    }
}
