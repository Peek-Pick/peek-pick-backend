package org.beep.sbpp.users.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.beep.sbpp.points.entities.PointStoreEntity;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.enums.CouponStatus;
import org.beep.sbpp.users.enums.Status;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class UserCouponDTO {

    private Long couponId;

    private String itemName; // 상품 이름

    private CouponStatus status;

    private LocalDateTime usedAt;

    private LocalDateTime expiredAt;

}
