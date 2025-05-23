package org.beep.sbpp.users.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.points.dto.PointStoreListDTO;
import org.beep.sbpp.users.dto.UserCouponDTO;
import org.beep.sbpp.users.repository.UserCouponRepository;
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
    public Page<UserCouponDTO> list(Long userId, Pageable pageable) {

        return userCouponRepository.couponList(userId, pageable);
    }


}
