package org.beep.sbpp.points.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.points.dto.UserCouponDTO;
import org.beep.sbpp.points.entities.PointEntity;
import org.beep.sbpp.points.enums.CouponStatus;
import org.beep.sbpp.points.repository.UserCouponRepository;
import org.beep.sbpp.users.entities.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserCouponServiceImpl implements UserCouponService {

    private final UserCouponRepository userCouponRepository;

    @Override
    public Page<UserCouponDTO> list(Long userId, String status, Pageable pageable) {

        // status가 null이거나 "ALL"이면 전체 조회
        if (status == null || status.isBlank() || status.equalsIgnoreCase("ALL")) {
            return userCouponRepository.couponList(userId, pageable);
        }
        // status 조건 필터링
        try {
            CouponStatus couponStatus = CouponStatus.valueOf(status);
            return userCouponRepository.couponListByStatus(userId, couponStatus, pageable);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid coupon status: {}", status);
            return Page.empty(pageable);
        }
    }

    //쿠폰함 쿠폰 갯수 출력 메서드
    public Long getUserCouponCount(Long userId) {

        return userCouponRepository.getUserCouponCount(userId);
    }


}
