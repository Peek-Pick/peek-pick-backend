package org.beep.sbpp.admin.points.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("productType")
    private PointProductType productType;

    @JsonProperty("imgUrl")
    private String imgUrl;
}
