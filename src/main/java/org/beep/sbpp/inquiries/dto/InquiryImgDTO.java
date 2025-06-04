package org.beep.sbpp.inquiries.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryImgDTO {
    private Long imgId;
    private Long InquiryId;
    private String imgUrl;
}
