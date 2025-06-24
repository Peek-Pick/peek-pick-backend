package org.beep.sbpp.admin.dashboard.dto;

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
public class AdminDashboardReportDTO {
    private Long reviewReportId;

    private Long reviewId;

    private Long reviewerId;

    private String nickname;

    private ReportReason reason;

    private LocalDateTime regDate;
}