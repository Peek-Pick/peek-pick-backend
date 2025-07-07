package org.beep.sbpp.products.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * 한국어 상품 데이터
 */
@Entity
@Table(name = "tbl_product_ko")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductKoEntity implements ProductLangEntity {

    @Id
    private Long productId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductBaseEntity productBase;

    /** 상품명(한국어) */
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /** 상품 설명(한국어) */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** 원재료(한국어) */
    @Column(name = "ingredients", columnDefinition = "TEXT")
    private String ingredients;

    /** 알레르기 정보(한국어) */
    @Column(name = "allergens", columnDefinition = "TEXT")
    private String allergens;

    /** 영양성분(한국어) */
    @Column(name = "nutrition", columnDefinition = "TEXT")
    private String nutrition;

    /** 카테고리(한국어) */
    @Column(name = "category", length = 255)
    private String category;

    /** 용량(한국어) */
    @Column(name = "volume", length = 255)
    private String volume;

    // ============================
    // ProductLangEntity 구현부
    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getIngredients() {
        return ingredients;
    }

    @Override
    public String getAllergens() {
        return allergens;
    }

    @Override
    public String getNutrition() {
        return nutrition;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String getVolume() {
        return volume;
    }
}
