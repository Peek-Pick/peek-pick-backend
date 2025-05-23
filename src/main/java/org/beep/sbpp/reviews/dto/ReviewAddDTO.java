package org.beep.sbpp.reviews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewAddDTO {
    private Long userId;

    private Integer score;
    private Integer recommendCnt;

    private String comment;

    private MultipartFile[] files;
}
