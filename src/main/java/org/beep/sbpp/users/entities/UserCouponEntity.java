package org.beep.sbpp.users.entities;

import jakarta.persistence.*;
import lombok.*;
import org.apache.catalina.User;
import org.beep.sbpp.points.entities.PointStoreEntity;
import org.beep.sbpp.users.enums.CouponStatus;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_user_coupon")
@EntityListeners(value = AuditingEntityListener.class)
@Getter
@ToString(exclude = {"user", "pointStore"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserCouponEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id", nullable = false)
    private Long couponId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pointstore_id", nullable = false)
    private PointStoreEntity pointStore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CouponStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    @Column(name = "expired_at")
    private LocalDateTime expiredAt;

}
