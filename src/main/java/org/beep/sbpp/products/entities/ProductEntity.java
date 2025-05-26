package org.beep.sbpp.products.entities;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_product")
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
    @Column(name = "ingredients", length = 255)
    private String ingredients;

    /** 알레르기 정보 */
    @Column(name = "allergens", length = 255)
    private String allergens;

    /** 영양 성분 */
    @Column(name = "nutrition", length = 255)
    private String nutrition;

    /** 즐겨찾기(Like) 수 */
    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    /** 소프트 삭제 플래그 */
    @Column(name = "is_delete", nullable = false)
    private Boolean isDelete;

    /** 등록 일시 */
    @Column(name = "reg_date", nullable = false, updatable = false)
    private LocalDateTime regDate;

    /** 수정 일시 */
    @Column(name = "mod_date", nullable = false)
    private LocalDateTime modDate;
}
