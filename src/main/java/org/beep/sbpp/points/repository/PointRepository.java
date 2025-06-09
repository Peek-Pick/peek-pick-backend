package org.beep.sbpp.points.repository;

import org.beep.sbpp.points.entities.PointEntity;
import org.beep.sbpp.users.entities.UserCouponEntity;
import org.beep.sbpp.users.enums.CouponStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointRepository extends JpaRepository<PointEntity, Long> {

    Optional<PointEntity> findByUser_UserId(Long userId);


}
