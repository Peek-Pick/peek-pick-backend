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
    private Integer recommendCnt;

    private String comment;

    private Boolean isHidden;

    private LocalDateTime regDate;
    private LocalDateTime modDate;

    private String nickname;
    private Boolean isLiked;

    private ReviewImgDTO image;

    private String imageUrl;
    private String name;
}