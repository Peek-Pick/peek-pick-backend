package org.beep.sbpp.search.document;

import lombok.*;
import org.beep.sbpp.products.entities.ProductEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

/**
 * Elasticsearch 인덱스에 저장될 상품 도큐먼트 클래스
 * - PostgreSQL의 ProductEntity 기반으로 필드 매핑
 * - 검색/정렬/필터링 목적에 맞게 Elasticsearch 필드 타입을 명시적으로 설정
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "products")
@Setting(settingPath = "/elasticsearch/products-settings.json") // 🔧 사용자 지정 설정 (nori analyzer 등)
@Mapping(mappingPath = "/elasticsearch/products-mappings.json") // 🧩 사용자 지정 매핑 (정확한 필드 타입)
public class ProductSearchDocument {

    @Id
    private String id; // 상품 PK (productId) → ES에서는 문자열 ID로 저장

    @Field(type = FieldType.Text)
    private String name; // 상품명 → 검색 대상

    @Field(type = FieldType.Text)
    private String description; // 상품 설명 → 검색 대상

    @Field(type = FieldType.Keyword)
    private String barcode; // 바코드 → 정확 일치 검색

    @Field(type = FieldType.Text)
    private String category; // 카테고리 → 필터 가능, 부분 검색 가능

    @Field(type = FieldType.Text)
    private String volume; // 용량 (검색 가능성은 낮지만 원본 유지 목적)

    @Field(type = FieldType.Text)
    private String ingredients; // 원재료 정보

    @Field(type = FieldType.Text)
    private String allergens; // 알레르기 정보

    @Field(type = FieldType.Text)
    private String nutrition; // 영양 정보

    @Field(type = FieldType.Keyword)
    private String imgThumbUrl;  // ✅ 추가

    @Field(type = FieldType.Boolean)
    private Boolean isDelete; // soft delete 여부 → 필터링에 사용

    @Field(type = FieldType.Integer)
    private Integer likeCount; // 좋아요 수 → 정렬 기준

    @Field(type = FieldType.Integer)
    private Integer reviewCount; // 리뷰 수 → 참고용

    @Field(type = FieldType.Double)
    private java.math.BigDecimal score; // 별점 → 정렬 기준

    @Field(type = FieldType.Text)
    private String mainTag; // 대표 태그 → 태그 검색 필터

    /**
     * ProductEntity → ProductSearchDocument 변환
     */
    public static ProductSearchDocument fromEntity(ProductEntity e) {
        return ProductSearchDocument.builder()
                .id(String.valueOf(e.getProductId()))
                .name(e.getName())
                .description(e.getDescription())
                .barcode(e.getBarcode())
                .category(e.getCategory())
                .volume(e.getVolume())
                .ingredients(e.getIngredients())
                .allergens(e.getAllergens())
                .nutrition(e.getNutrition())
                .imgThumbUrl(e.getImgThumbUrl()) // ✅ 추가
                .isDelete(e.getIsDelete())
                .likeCount(e.getLikeCount())
                .reviewCount(e.getReviewCount())
                .score(e.getScore())
                .mainTag(e.getMainTag())
                .build();
    }
}
