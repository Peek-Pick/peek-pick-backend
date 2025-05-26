package org.beep.sbpp.reviews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewDetailDTO {
    private Long reviewId;

    private Long userId;

    private Integer score;
    private Integer recommendCnt;

    private String comment;

    private Boolean isHidden;
    private Boolean isDeleted;

    private List<ReviewImgDTO> images;

    private LocalDateTime regDate;
    private LocalDateTime modDate;

    private String nickname;
    private Boolean isLiked;
}