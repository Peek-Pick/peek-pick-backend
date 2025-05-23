package org.beep.sbpp.points.service;

import org.beep.sbpp.users.entities.UserCouponEntity;

public interface PointService {

    int redeemPoints(Long userId, Long pointStoreId);
}
