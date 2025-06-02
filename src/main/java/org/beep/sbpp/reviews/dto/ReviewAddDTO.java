package org.beep.sbpp.reviews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewAddDTO {
    private Long userId;
    private Long productId;

    private Integer score;
    private String comment;
    private MultipartFile[] files;
    private List<Long> tagIdList;

    private Integer recommendCnt;
}