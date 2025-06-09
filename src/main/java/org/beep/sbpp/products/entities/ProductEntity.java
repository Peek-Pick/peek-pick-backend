package org.beep.sbpp.products.entities;

import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.common.BaseEntity;

import java.math.BigDecimal;

@Entity
@Table(
        name = "tbl_product",
        indexes = {
                @Index(name = "idx_tbl_product_barcode", columnList = "barcode")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uq_tbl_product_barcode", columnNames = "barcode")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity extends BaseEntity {

    /** PK: auto_increment */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id", nullable = false)
    private Long productId;

    /** 바코드 */
    @Column(name = "barcode", nullable = false, length = 255)
    private String barcode;

    /** 상품 설명 (세부정보) */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** 상품명 */
    @Column(name = "name", nullable = false, length = 255)
    private String name;

    /** 카테고리 */
    @Column(name = "category", length = 255)
    private String category;

    /** 용량 */
    @Column(name = "volume", length = 255)
    private String volume;

    /** 이미지 URL */
    @Column(name = "img_url", length = 255)
    private String imgUrl;

    /** 원재료 */
    @Column(name = "ingredients", columnDefinition = "TEXT")
    private String ingredients;

    /** 알레르기 정보 */
    @Column(name = "allergens", length = 255)
    private String allergens;

    /** 영양 성분 */
    @Column(name = "nutrition", columnDefinition = "TEXT")
    private String nutrition;

    /** 즐겨찾기(Like) 수 */
    @Builder.Default
    @Column(name = "like_count", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer likeCount = 0;

    /** 리뷰 수 */
    @Builder.Default
    @Column(name = "review_count", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer reviewCount = 0;

    /** 별점 (0.0 ~ 5.0), null 허용 */
    @Column(name = "score", precision = 2, scale = 1)
    private BigDecimal score;

    /** 소프트 삭제 플래그 */
    @Builder.Default
    @Column(name = "is_delete", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDelete = false;

    /**
     * INSERT 직전에 기본값 보장
     */
    @PrePersist
    private void prePersist() {
        if (this.likeCount == null)    this.likeCount   = 0;
        if (this.reviewCount == null)  this.reviewCount = 0;
        if (this.isDelete == null)     this.isDelete    = false;
        // regDate, modDate는 BaseEntity의 @CreatedDate/@LastModifiedDate가 자동 세팅
    }

    // 수정 시에는 modDate만 AuditingEntityListener가 자동 갱신하므로 별도 처리 불필요
    @Column(name = "main_tag", length = 100)
    private String mainTag;

    // regDate, modDate 필드 및 자동 관리 로직은 BaseEntity 에서 상속받습니다.

}
