package org.beep.sbpp.admin.inquiries.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class InquiryReplyRequestDTO {
    @NotBlank(message = "답변 내용은 필수입니다.")
    private String content;
}
