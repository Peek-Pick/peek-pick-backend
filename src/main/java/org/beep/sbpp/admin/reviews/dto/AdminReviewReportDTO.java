package org.beep.sbpp.admin.reviews.dto;

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
public class AdminReviewReportDTO {
    private Long reviewReportId;
    private Long userId;
    private Long reviewId;
    private Long reviewerId;

    private ReportReason reason;

    private LocalDateTime regDate;
}