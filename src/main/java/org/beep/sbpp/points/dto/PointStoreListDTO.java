package org.beep.sbpp.points.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beep.sbpp.points.enums.PointProductType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointStoreListDTO {

    private Long pointstoreId;

    private String item;

    private int price;

    private PointProductType productType;

    // 이건 업로드된 이미지 경로 저장용
    private String imgUrl;

}
