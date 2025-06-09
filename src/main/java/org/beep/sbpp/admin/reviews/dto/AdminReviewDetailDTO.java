package org.beep.sbpp.admin.reviews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beep.sbpp.reviews.dto.ReviewImgDTO;
import org.beep.sbpp.tags.dto.TagDTO;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReviewDetailDTO {
    private Long reviewId;

    private Long userId;
    private Long productId;

    private Integer score;
    private String comment;
    private List<ReviewImgDTO> images;
    private List<TagDTO> tagList;

    private LocalDateTime regDate;
    private LocalDateTime modDate;

    private String nickname;
    private String profileImageUrl;

    private Integer recommendCnt;
    private Integer reportCnt;
    private Boolean isHidden;

    private String name;
}