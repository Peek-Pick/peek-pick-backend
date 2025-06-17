package org.beep.sbpp.admin.reviews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminReviewSimpleDTO {
    private Long reviewId;

    private Long userId;
    private Long productId;

    private LocalDateTime regDate;
    private LocalDateTime modDate;

    private String nickname;
    private String name;
}