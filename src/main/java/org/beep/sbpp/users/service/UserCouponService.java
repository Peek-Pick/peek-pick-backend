package org.beep.sbpp.users.service;

import org.beep.sbpp.users.dto.UserCouponDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserCouponService {
    Page<UserCouponDTO> list(Long userId, Pageable pageable);
}
