package org.beep.sbpp.admin.products.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beep.sbpp.products.entities.ProductEntity;

/**
 * 관리자용 상품 생성/수정 요청 DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequestDto {

    @NotBlank(message = "바코드는 필수 입력값입니다.")
    private String barcode;

    @NotBlank(message = "상품명은 필수 입력값입니다.")
    @Size(max = 100, message = "상품명은 최대 100자까지 가능합니다.")
    private String name;

    private String category;
    private String description;
    private String volume;
    private String ingredients;
    private String allergens;
    private String nutrition;

    /** 이미지 URL (선택) */
    private String imgUrl;

    /** soft-delete 토글 */
    private Boolean isDelete;

    /**
     * 이 DTO를 ProductEntity로 변환.
     * create 시에만 사용하며, update 시에는 service 레이어에서 entity를 직접 수정.
     */
    public ProductEntity toEntity() {
        ProductEntity.ProductEntityBuilder b = ProductEntity.builder()
                .barcode(this.barcode)
                .name(this.name)
                .category(this.category)
                .description(this.description)
                .volume(this.volume)
                .ingredients(this.ingredients)
                .allergens(this.allergens)
                .nutrition(this.nutrition);

        if (this.imgUrl != null && !this.imgUrl.isEmpty()) {
            b.imgUrl(this.imgUrl);
        }
        // create 시에도 isDelete 값이 지정되어 있으면 반영
        if (this.isDelete != null) {
            b.isDelete(this.isDelete);
        }

        return b.build();
    }
}
