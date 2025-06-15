package org.beep.sbpp.points.entities;

import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.points.enums.PointProductType;
import org.beep.sbpp.common.BaseEntity;

@Entity
@Table(name = "tbl_point_store")
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointStoreEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pointstore_id", nullable = false)
    private Long pointstoreId;

    @Column(name = "item", nullable = false, length = 255)
    private String item;

    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "description")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "product_type", nullable = false)
    private PointProductType productType;

    @Column(name = "img_url", length = 255)
    private String imgUrl;

    @Column(name = "is_hidden")
    private boolean isHidden = false;


    // 상품 이름 변경
    public void changePrice(int price) {
        this.price = price;
    }
    // 상품 가격 변경
    public void changePname(String item) {
        this.item = item;
    }
    // 상품 설명 변경
    public void changeDesc(String pdesc) {
        this.description = description;
    }
    // 상품 타입 변경
    public void changeType(PointProductType productType) {
        this.productType = productType;
    }
    // 상품 이미지 변경
    public void changeImg(String imgUrl) {
        this.imgUrl = imgUrl;
    }
    //상품 삭제 처리
    public void softDelete() {
        this.isHidden = !isHidden;
    }




}
