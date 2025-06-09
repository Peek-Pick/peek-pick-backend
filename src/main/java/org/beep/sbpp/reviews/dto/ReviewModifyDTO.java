package org.beep.sbpp.reviews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beep.sbpp.tags.dto.TagDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewModifyDTO {
    private Long reviewId;

    private String comment;
    private Integer score;

    private List<Long> deleteImgIds;
    private MultipartFile[] files;

    private List<Long> deleteTagIds;
    private List<Long> newTagIds;
}