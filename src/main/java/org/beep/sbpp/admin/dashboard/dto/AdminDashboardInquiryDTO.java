package org.beep.sbpp.admin.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beep.sbpp.inquiries.enums.InquiryStatus;
import org.beep.sbpp.inquiries.enums.InquiryType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardInquiryDTO {
    private Long inquiryId;

    private Long userId;
    private String nickname;

    private String content;
    private InquiryType type;
    private InquiryStatus status;

    private LocalDateTime regDate;
}