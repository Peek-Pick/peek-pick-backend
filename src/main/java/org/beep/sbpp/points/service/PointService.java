package org.beep.sbpp.points.service;

import org.beep.sbpp.points.dto.PointLogsDTO;
import org.beep.sbpp.users.entities.UserCouponEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PointService {

    int redeemPoints(Long userId, Long pointStoreId);

    Page<PointLogsDTO> pointLogsList(Long userId, Pageable pageable);
}
