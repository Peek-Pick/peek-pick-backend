package org.beep.sbpp.points.service;

import org.beep.sbpp.points.dto.UserCouponDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserCouponService {
    Page<UserCouponDTO> list(Long userId, String status, Pageable pageable);
}
