// src/main/java/org/beep/sbpp/admin/products/dto/ProductRequestDTO.java
package org.beep.sbpp.admin.products.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.beep.sbpp.products.entities.*;

import java.util.Optional;

/**
 * 관리자용 상품 생성/수정 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDTO {

    @NotBlank(message = "바코드는 필수 입력값입니다.")
    private String barcode;

    @NotBlank(message = "상품명은 필수 입력값입니다.")
    @Size(max = 100, message = "상품명은 최대 100자까지 가능합니다.")
    private String name;

    /** 언어별 category, description, volume 등도 langEntity 로 옮겨갑니다 */
    private String category;
    private String description;
    private String volume;
    private String ingredients;
    private String allergens;
    private String nutrition;

    /** soft-delete 토글 */
    private Boolean isDelete;

    /**
     * ProductBaseEntity 변환 메서드
     * - 공통 필드만 세팅
     */
    public ProductBaseEntity toBaseEntity() {
        return ProductBaseEntity.builder()
                .barcode(this.barcode)
                .imgUrl(null)           // 이미지 URL은 Service에서 설정
                .imgThumbUrl(null)
                .mainTag(null)          // 대표 태그는 이후 로직에서 결정
                .likeCount(0)
                .reviewCount(0)
                .score(null)
                .isDelete(Optional.ofNullable(this.isDelete).orElse(false))
                .build();
    }

    /**
     * 언어별 Entity 변환 메서드
     * @param base 생성된 ProductBaseEntity
     * @param lang "ko", "en", "ja"
     */
    public ProductLangEntity toLangEntity(ProductBaseEntity base, String lang) {
        return switch(lang.toLowerCase().split("[-_]")[0]) {
            case "ko" -> ProductKoEntity.builder()
                    .productBase(base)
                    .name(this.name)
                    .category(this.category)
                    .description(this.description)
                    .volume(this.volume)
                    .ingredients(this.ingredients)
                    .allergens(this.allergens)
                    .nutrition(this.nutrition)
                    .build();
            case "en" -> ProductEnEntity.builder()
                    .productBase(base)
                    .name(this.name)
                    .category(this.category)
                    .description(this.description)
                    .volume(this.volume)
                    .ingredients(this.ingredients)
                    .allergens(this.allergens)
                    .nutrition(this.nutrition)
                    .build();
            case "ja" -> ProductJaEntity.builder()
                    .productBase(base)
                    .name(this.name)
                    .category(this.category)
                    .description(this.description)
                    .volume(this.volume)
                    .ingredients(this.ingredients)
                    .allergens(this.allergens)
                    .nutrition(this.nutrition)
                    .build();
            default -> throw new IllegalArgumentException("지원하지 않는 언어: " + lang);
        };
    }

    /**
     * BaseEntity 수정용 메서드
     */
    public void updateBaseEntity(ProductBaseEntity base) {
        base.setBarcode(this.barcode);
        if (this.isDelete != null) {
            base.setIsDelete(this.isDelete);
        }
    }

    /**
     * LangEntity 수정용 메서드
     */
    public void updateLangEntity(ProductLangEntity langE) {
        langE.setName(this.name);
        langE.setCategory(this.category);
        langE.setDescription(this.description);
        langE.setVolume(this.volume);
        langE.setIngredients(this.ingredients);
        langE.setAllergens(this.allergens);
        langE.setNutrition(this.nutrition);
    }
}
