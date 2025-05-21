package org.beep.sbpp.reviews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beep.sbpp.reviews.enums.ReportReason;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReportDTO {
    private Long reviewId;
    private Long userId;

    private ReportReason reason;

    private LocalDateTime regDate;
}