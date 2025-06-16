package org.beep.sbpp.admin.inquiries.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InquiryReplyResponseDTO {
    private String content;
}