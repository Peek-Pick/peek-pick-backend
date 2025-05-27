package org.beep.sbpp.products.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

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
public class ProductEntity {

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
    @Column(name = "like_count", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer likeCount = 0;

    /** 소프트 삭제 플래그 */
    @Column(name = "is_delete", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean isDelete = false;

    /** 등록 일시 */
    @CreationTimestamp
    @Column(name = "reg_date", nullable = false, updatable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime regDate;

    /** 수정 일시 */
    @UpdateTimestamp
    @Column(name = "mod_date", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime modDate;

    /** 새로운 엔티티 저장 직전 호출 */
    @PrePersist
    public void prePersist() {
        if (likeCount == null) {
            likeCount = 0;
        }
        if (isDelete == null) {
            isDelete = false;
        }
        LocalDateTime now = LocalDateTime.now();
        regDate = now;
        modDate = now;
    }

    /** 엔티티 업데이트 직전 호출 */
    @PreUpdate
    public void preUpdate() {
        modDate = LocalDateTime.now();
    }
}
