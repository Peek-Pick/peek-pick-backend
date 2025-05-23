package org.beep.sbpp.users.repository;

import org.beep.sbpp.users.dto.UserCouponDTO;
import org.beep.sbpp.users.entities.UserCouponEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserCouponRepository extends JpaRepository<UserCouponEntity, Long>  {

    //목록
    @Query("select new org.beep.sbpp.users.dto.UserCouponDTO(p.couponId, p.pointStore.item, p.status, p.usedAt, p.expiredAt) " +
            "from UserCouponEntity p " +
            "where p.user.userId = :userId")
    Page<UserCouponDTO> couponList(@Param("userId") Long userId, Pageable pageable);
}
