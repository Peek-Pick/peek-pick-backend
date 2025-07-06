package org.beep.sbpp.products.entities;

import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.common.BaseEntity;

import java.math.BigDecimal;

/**
 * Base product information that does not depend on language.
 */
@Entity
@Table(name = "tbl_product_base")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBaseEntity extends BaseEntity {

    /** PK: auto_increment */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long productId;

    /** Barcode */
    @Column(name = "barcode", length = 255)
    private String barcode;

    /** Volume */
    @Column(name = "volume", length = 255)
    private String volume;

    /** Image URL */
    @Column(name = "img_url", length = 255)
    private String imgUrl;

    /** Thumbnail image URL */
    @Column(name = "img_thumb_url", length = 255)
    private String imgThumbUrl;

    /** Ingredients */
    @Column(name = "ingredients", columnDefinition = "TEXT")
    private String ingredients;

    /** Allergens */
    @Column(name = "allergens", columnDefinition = "TEXT")
    private String allergens;

    /** Nutrition information */
    @Column(name = "nutrition", columnDefinition = "TEXT")
    private String nutrition;

    /** Like count */
    @Builder.Default
    @Column(name = "like_count", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer likeCount = 0;

    /** Review count */
    @Builder.Default
    @Column(name = "review_count", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer reviewCount = 0;

    /** Score (0.0 ~ 5.0) */
    @Builder.Default
    @Column(name = "score", precision = 2, scale = 1, columnDefinition = "DECIMAL(2,1) DEFAULT 0.0")
    private BigDecimal score = BigDecimal.valueOf(0.0);

    /** Soft delete flag */
    @Builder.Default
    @Column(name = "is_delete", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDelete = false;

    @Column(name = "main_tag", length = 100)
    private String mainTag;

    /** Ensure default values right before insert */
    @PrePersist
    private void prePersist() {
        if (this.likeCount == null) this.likeCount = 0;
        if (this.reviewCount == null) this.reviewCount = 0;
        if (this.isDelete == null) this.isDelete = false;
        if (this.score == null) this.score = BigDecimal.valueOf(0.0);
    }
}
