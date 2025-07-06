package org.beep.sbpp.search.document;

import lombok.*;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.math.BigDecimal;

/**
 * Elasticsearch 색인을 위한 상품 도큐먼트 클래스
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

    @Field(type = FieldType.Long)
    private Long productId;

    // ✅ name: nori 분석기 + edge_ngram 자동완성 서브필드
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "korean_analyzer"),
            otherFields = {
                    @InnerField(suffix = "autocomplete", type = FieldType.Text, analyzer = "autocomplete_index", searchAnalyzer = "autocomplete_search")
            }
    )
    private String name;

    @Field(type = FieldType.Text, analyzer = "korean_analyzer")
    private String description;

    @Field(type = FieldType.Keyword)
    private String barcode;

    // ✅ category: filter와 검색 동시 지원 (keyword 서브필드 필수)
    @MultiField(
            mainField = @Field(type = FieldType.Text, analyzer = "korean_analyzer"),
            otherFields = {
                    @InnerField(suffix = "keyword", type = FieldType.Keyword)
            }
    )
    private String category;

    @Field(type = FieldType.Text)
    private String volume;

    @Field(type = FieldType.Text)
    private String ingredients;

    @Field(type = FieldType.Text)
    private String allergens;

    @Field(type = FieldType.Text)
    private String nutrition;

    @Field(type = FieldType.Keyword)
    private String imgThumbUrl;

    @Field(type = FieldType.Boolean)
    private Boolean isDelete;

    @Field(type = FieldType.Integer)
    private Integer likeCount;

    @Field(type = FieldType.Integer)
    private Integer reviewCount;

    @Field(type = FieldType.Double)
    private BigDecimal score;

    @Field(type = FieldType.Text)
    private String mainTag;

    /**
     * ProductBaseEntity -> ProductSearchDocument 변환 메서드
     */
    public static ProductSearchDocument fromEntity(ProductBaseEntity e) {
        return ProductSearchDocument.builder()
                .id(String.valueOf(e.getProductId()))
                .productId(e.getProductId())
                .name(e.getName())
                .description(e.getDescription())
                .barcode(e.getBarcode())
                .category(e.getCategory())
                .volume(e.getVolume())
                .ingredients(e.getIngredients())
                .allergens(e.getAllergens())
                .nutrition(e.getNutrition())
                .imgThumbUrl(e.getImgThumbUrl())
                .isDelete(e.getIsDelete())
                .likeCount(e.getLikeCount())
                .reviewCount(e.getReviewCount())
                .score(e.getScore())
                .mainTag(e.getMainTag())
                .build();
    }
}
