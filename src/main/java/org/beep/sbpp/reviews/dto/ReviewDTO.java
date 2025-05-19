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
public class ReviewDTO {
    private Long reviewId;

    private Integer score;
    private Integer recommendCnt;

    private String comment;

    private Boolean isHidden;
    private Boolean isDeleted;

    private LocalDateTime regDate;
    private LocalDateTime modDate;
}