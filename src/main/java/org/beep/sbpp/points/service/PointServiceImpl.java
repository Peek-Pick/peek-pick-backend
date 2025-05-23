package org.beep.sbpp.points.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.points.entities.PointEntity;
import org.beep.sbpp.points.entities.PointLogsEntity;
import org.beep.sbpp.points.entities.PointStoreEntity;
import org.beep.sbpp.points.enums.PointLogsDesc;
import org.beep.sbpp.points.enums.PointLogsType;
import org.beep.sbpp.points.repository.PointLogsRepository;
import org.beep.sbpp.points.repository.PointRepository;
import org.beep.sbpp.points.repository.PointStoreRepository;
import org.beep.sbpp.users.entities.UserCouponEntity;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.enums.CouponStatus;
import org.beep.sbpp.users.repository.UserCouponRepository;
import org.beep.sbpp.users.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PointServiceImpl implements PointService{

    private final PointRepository pointRepository;
    private final PointLogsRepository pointLogsRepository;
    private final PointStoreRepository pointStoreRepository;
    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;

    @Override
    @Transactional
    // 쿠폰 구매 처리 메서드 - pointStoreId로 가격 조회 후 포인트 차감 + 쿠폰 지급
    public int redeemPoints(Long userId, Long pointStoreId) {

        // 이메일로 유저 정보 조회해서 userId 얻기
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("유저 정보 없음"));

        // 1. 상품 조회
        PointStoreEntity pointStore = pointStoreRepository.findById(pointStoreId)
                .orElseThrow(() -> new RuntimeException("상품 정보 없음"));

        int redeemAmount = pointStore.getPrice();

        // 2. 유저 포인트 조회
        PointEntity pointEntity = pointRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("포인트 정보 없음"));

        // 3. 포인트 충분한지 체크
        if (pointEntity.getAmount() < redeemAmount) {
            throw new IllegalArgumentException("포인트 부족");
        }

        // 4. 포인트 차감
        pointEntity.changeAmount(pointEntity.getAmount() - redeemAmount);
        pointRepository.save(pointEntity);

        // 5. 포인트 사용 로그 기록
        PointLogsEntity log = PointLogsEntity.builder()
                .user(pointEntity.getUser())
                .amount(redeemAmount)
                .type(PointLogsType.USE)
                .description(PointLogsDesc.SHOP_USE)
                .build();
        pointLogsRepository.save(log);

        // 6. 쿠폰 생성 및 저장
        UserCouponEntity userCoupon = UserCouponEntity.builder()
                .user(user)
                .pointStore(pointStore)
                .status(CouponStatus.AVAILABLE)
                .expiredAt(LocalDateTime.now().plusMonths(1))
                .build();
        userCouponRepository.save(userCoupon);

        // 7. 남은 포인트 반환
        return pointEntity.getAmount();
    }


}
