package org.beep.sbpp.inquiries.dto;

import lombok.*;
import org.beep.sbpp.inquiries.enums.InquiryStatus;
import org.beep.sbpp.inquiries.enums.InquiryType;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InquiryResponseDTO {
    private Long inquiryId;
    private Long userId;
    private String userNickname;
    private String title;
    private String content;
    private InquiryType type;
    private InquiryStatus status;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private List<String> imgUrls; // “/upload/inquiries/파일명.jpg”
}
