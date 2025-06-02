package org.beep.sbpp.reviews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewSimpleDTO {
    private Long reviewId;

    private Long userId;
    private Long productId;

    private Integer score;
    private String comment;
    private ReviewImgDTO image;

    private LocalDateTime regDate;
    private LocalDateTime modDate;

    private String nickname;
    private Integer recommendCnt;

    private Boolean isHidden;

    private String name;
    private String imageUrl;

    private Boolean isLiked;
}