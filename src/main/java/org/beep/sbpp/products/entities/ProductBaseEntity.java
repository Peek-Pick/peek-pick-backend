package org.beep.sbpp.products.entities;

import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.common.BaseEntity;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;

import java.math.BigDecimal;

@Entity
@Table(
        name = "tbl_product_base",
        indexes = {@Index(name = "idx_tbl_product_base_barcode", columnList = "barcode")},
        uniqueConstraints = {@UniqueConstraint(name = "uq_tbl_product_base_barcode", columnNames = "barcode")}
)      // <- 한 번에 최대 50개씩 묶어서 조회
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBaseEntity extends BaseEntity {

    /** PK: auto_increment */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /** 바코드 */
    @Column(name = "barcode", nullable = false, length = 255)
    private String barcode;

    /** 이미지 URL */
    @Column(name = "img_url", length = 255)
    private String imgUrl;

    /** 이미지 썸네일 URL */
    @Column(name = "img_thumb_url", length = 255)
    private String imgThumbUrl;

    /** 즐겨찾기(Like) 수 */
    @Builder.Default
    @Column(name = "like_count", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer likeCount = 0;

    /** 리뷰 수 */
    @Builder.Default
    @Column(name = "review_count", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer reviewCount = 0;

    /** 별점 (0.0 ~ 5.0), null 허용 */
    @Column(name = "score", precision = 2, scale = 1, columnDefinition = "DECIMAL(2,1)")
    @ColumnDefault("0.0")
    @Builder.Default
    private BigDecimal score = BigDecimal.valueOf(0.0);

    /** 소프트 삭제 플래그 */
    @Builder.Default
    @Column(name = "is_delete", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDelete = false;

    /** 모든 언어 공통 Main 태그 */
    @Column(name = "main_tag", length = 100)
    private String mainTag;

    @PrePersist
    private void prePersist() {
        if (this.likeCount == null) this.likeCount = 0;
        if (this.reviewCount == null) this.reviewCount = 0;
        if (this.isDelete == null) this.isDelete = false;
        if (this.score == null) this.score = BigDecimal.valueOf(0.0);
    }

    /** 한국어 엔티티 */
    @OneToOne(mappedBy = "productBase", fetch = FetchType.LAZY)
    private ProductKoEntity koEntity;

    /** 영어 엔티티 */
    @OneToOne(mappedBy = "productBase", fetch = FetchType.LAZY)
    private ProductEnEntity enEntity;

    /** 일본어 엔티티 */
    @OneToOne(mappedBy = "productBase", fetch = FetchType.LAZY)
    private ProductJaEntity jaEntity;
}
