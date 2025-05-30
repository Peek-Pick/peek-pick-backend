package org.beep.sbpp.inquiries.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.beep.sbpp.inquiries.enums.InquiryStatus;
import org.beep.sbpp.inquiries.enums.InquiryType;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryRequestDTO {
    @NotNull
    private Long userId;
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotNull
    private InquiryType type;
    @Builder.Default
    private InquiryStatus status = InquiryStatus.PENDING;

    private List<String> imgUrls;
}
