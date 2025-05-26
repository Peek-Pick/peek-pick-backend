package org.beep.sbpp.users.repository;

import org.beep.sbpp.users.dto.UserCouponDTO;
import org.beep.sbpp.users.entities.UserCouponEntity;
import org.beep.sbpp.users.enums.CouponStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCouponRepository extends JpaRepository<UserCouponEntity, Long>  {

    //쿠폰함 목록
    @Query("select new org.beep.sbpp.users.dto.UserCouponDTO(p.couponId, p.pointStore.item, p.status, p.pointStore.imgUrl, p.usedAt, p.expiredAt) " +
            "from UserCouponEntity p " +
            "where p.user.userId = :userId")
    Page<UserCouponDTO> couponList(@Param("userId") Long userId, Pageable pageable);

    //쿠폰함 목록 - 상태 필터링
    @Query("select new org.beep.sbpp.users.dto.UserCouponDTO(p.couponId, p.pointStore.item, p.status, p.pointStore.imgUrl, p.usedAt, p.expiredAt) " +
            "from UserCouponEntity p " +
            "where p.user.userId = :userId and p.status = :status")
    Page<UserCouponDTO> couponListByStatus(@Param("userId") Long userId, @Param("status") CouponStatus status, Pageable pageable);


}
