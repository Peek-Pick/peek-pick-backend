package org.beep.sbpp.points.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.beep.sbpp.points.enums.CouponStatus;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class UserCouponDTO {

    private Long couponId;

    private String itemName; // 상품 이름

    private CouponStatus status;

    private String couponImg;

    private LocalDateTime usedAt;

    private LocalDateTime expiredAt;



}


