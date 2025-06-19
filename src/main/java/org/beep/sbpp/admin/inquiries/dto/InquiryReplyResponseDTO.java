package org.beep.sbpp.admin.inquiries.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InquiryReplyResponseDTO {
    private String content;
    private LocalDateTime regDate;
}