package org.beep.sbpp.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMyPageResponseDTO {
    private String profileImgUrl;
    private String nickname;
    private int point;

    private int wishlistedCount;
    private int reviewCount;
    private int couponCount;
    private int barcodeHistoryCount;
}
