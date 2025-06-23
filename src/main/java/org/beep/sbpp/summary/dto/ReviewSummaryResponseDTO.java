package org.beep.sbpp.summary.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ReviewSummaryResponseDTO {
    private Long productId;
    private int percent;
    private String positiveSummary;
    private String negativeSummary;
}
