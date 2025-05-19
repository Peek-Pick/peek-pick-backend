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
public class ReviewLikeDTO {
    private Long reviewLikeId;

    private Long reviewId;
    private Long userId;

    private Boolean isDelete;

    private LocalDateTime regDate;
    private LocalDateTime modDate;
}