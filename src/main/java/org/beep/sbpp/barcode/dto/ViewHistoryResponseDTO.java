package org.beep.sbpp.barcode.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ViewHistoryResponseDTO {
    private Long viewId;
    private LocalDateTime regDate;
    @JsonProperty("isBarcodeHistory")
    private boolean isBarcodeHistory;
    @JsonProperty("isReview")
    private boolean isReview;
    private String barcode;
    private Long productId;
    private Long userId;
    private String productName;
    private String productImageUrl;
}
