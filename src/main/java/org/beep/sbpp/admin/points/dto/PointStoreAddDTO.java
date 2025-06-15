package org.beep.sbpp.admin.points.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beep.sbpp.points.enums.PointProductType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointStoreAddDTO {

    private Long pointstoreId;

    private String item;

    private int price;

    private String description;

    private PointProductType productType;

    // 이건 업로드된 이미지 경로 저장용
    private String imgUrl;

    // 이건 업로드된 실제 파일 받기용
    private List<MultipartFile> imageFile;

}
