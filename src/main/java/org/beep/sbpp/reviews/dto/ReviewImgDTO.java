package org.beep.sbpp.reviews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewImgDTO {
    private Long reviewId;

    private String imgUrl;
}