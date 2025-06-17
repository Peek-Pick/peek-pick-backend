package org.beep.sbpp.points.repository;

import org.beep.sbpp.points.dto.UserCouponDTO;
import org.beep.sbpp.points.entities.UserCouponEntity;
import org.beep.sbpp.points.enums.CouponStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCouponRepository extends JpaRepository<UserCouponEntity, Long>  {

    //쿠폰함 목록
    @Query("select new org.beep.sbpp.points.dto.UserCouponDTO(p.couponId, p.pointStore.item, p.status, p.pointStore.imgUrl, p.usedAt, p.expiredAt) " +
            "from UserCouponEntity p " +
            "where p.user.userId = :userId")
    Page<UserCouponDTO> couponList(@Param("userId") Long userId, Pageable pageable);

    //쿠폰함 목록 - 상태 필터링
    @Query("select new org.beep.sbpp.points.dto.UserCouponDTO(p.couponId, p.pointStore.item, p.status, p.pointStore.imgUrl, p.usedAt, p.expiredAt) " +
            "from UserCouponEntity p " +
            "where p.user.userId = :userId and p.status = :status")
    Page<UserCouponDTO> couponListByStatus(@Param("userId") Long userId, @Param("status") CouponStatus status, Pageable pageable);

    @Query("select count(p) " +
            "from UserCouponEntity p " +
            "where p.user.userId = :userId and p.status = org.beep.sbpp.points.enums.CouponStatus.AVAILABLE")
    Long getUserCouponCount(@Param("userId") Long userId);

}
