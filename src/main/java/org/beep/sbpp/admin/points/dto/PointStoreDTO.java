package org.beep.sbpp.admin.points.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beep.sbpp.points.enums.PointProductType;
import org.beep.sbpp.points.entities.PointStoreEntity;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointStoreDTO {

    private Long pointstoreId;

    private String item;

    private int price;

    private String description;

    private PointProductType productType;

    // 이건 업로드된 이미지 경로 저장용
    private String imgUrl;

    private boolean isHidden;


    public PointStoreDTO(PointStoreEntity entity) {
        this.pointstoreId = entity.getPointstoreId();
        this.item = entity.getItem();
        this.price = entity.getPrice();
        this.description = entity.getDescription();
        this.productType = entity.getProductType();
        this.imgUrl = entity.getImgUrl();
        this.isHidden = entity.isHidden();
    }

}
