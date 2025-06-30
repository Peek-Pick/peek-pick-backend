package org.beep.sbpp.points.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.admin.points.dto.PointStoreDTO;
import org.beep.sbpp.admin.points.dto.PointStoreListDTO;
import org.beep.sbpp.points.dto.PointLogsDTO;
import org.beep.sbpp.points.entities.PointEntity;
import org.beep.sbpp.points.entities.PointLogsEntity;
import org.beep.sbpp.points.entities.PointStoreEntity;
import org.beep.sbpp.points.enums.PointLogsDesc;
import org.beep.sbpp.points.enums.PointLogsType;
import org.beep.sbpp.points.enums.PointProductType;
import org.beep.sbpp.points.repository.PointLogsRepository;
import org.beep.sbpp.points.repository.PointRepository;
import org.beep.sbpp.admin.points.repository.AdminPointRepository;
import org.beep.sbpp.points.entities.UserCouponEntity;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.points.enums.CouponStatus;
import org.beep.sbpp.points.repository.UserCouponRepository;
import org.beep.sbpp.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PointServiceImpl implements PointService{

    private final PointRepository pointRepository;
    private final PointLogsRepository pointLogsRepository;
    private final AdminPointRepository pointStoreRepository;
    private final UserRepository userRepository;
    private final UserCouponRepository userCouponRepository;


    @Override
    public Page<PointStoreListDTO> list(String productType, Pageable pageable) {

        // status가 null이거나 "ALL"이면 전체 조회
        if (productType == null || productType.isBlank() || productType.equalsIgnoreCase("ALL")) {
            return pointStoreRepository.list(pageable);
        }
        // status 조건 필터링
        try {
            PointProductType couponType = PointProductType.valueOf(productType);
            return pointStoreRepository.listByType(couponType, pageable);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid coupon status: {}", productType);
            return Page.empty(pageable);
        }
    }

    @Override
    public PointStoreDTO read(Long pointstoreId) {

        return new PointStoreDTO(pointStoreRepository.selectOne(pointstoreId));
    }

    @Override
    @Transactional
    // 쿠폰 구매 처리 메서드 - pointStoreId로 가격 조회 후 포인트 차감 + 쿠폰 지급
    public int redeemPoints(Long userId, Long pointStoreId) {

        // 유저 정보 조회
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

    @Override
    @Transactional
    // 포인트 획득 메서드 - (일반리뷰 작성: 10p, 포토리뷰 작성: 50p)
    public int earnPoints(Long userId, int earnAmount, PointLogsDesc description) {

        // 0. 리뷰 작성 시 획득은 하루 5회 제한
        if (description == PointLogsDesc.REVIEW_GENERAL || description == PointLogsDesc.REVIEW_PHOTO) {

            LocalDate today = LocalDate.now();
            LocalDateTime startOfDay = today.atStartOfDay();
            LocalDateTime endOfDay = today.atTime(LocalTime.MAX);

            int todayCount = pointLogsRepository.countReviewEarn(
                    userId, description, PointLogsType.EARN, startOfDay, endOfDay
            );

            if (todayCount >= 5) {
                // 초과 시 적립하지 않고 현재 보유 포인트 반환
                PointEntity pointEntity = pointRepository.findByUser_UserId(userId)
                        .orElseThrow(() -> new RuntimeException("포인트 정보 없음"));

                return pointEntity.getAmount();
            }
        }

        // 1. 유저 정보 조회
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("유저 정보 없음"));

        // 2. 포인트 증가
        PointEntity pointEntity = pointRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new RuntimeException("포인트 정보 없음"));
        
        pointEntity.changeAmount(pointEntity.getAmount() + earnAmount);
        pointRepository.save(pointEntity);

        // 3. 포인트 획득 로그 기록
        PointLogsEntity log = PointLogsEntity.builder()
                .user(pointEntity.getUser())
                .amount(earnAmount)
                .type(PointLogsType.EARN)
                .description(description) // enum 값 넘겨야 함
                .build();
        pointLogsRepository.save(log);

        // 4. 남은 포인트 반환
        return pointEntity.getAmount();
    }


    //포인트 로그 내역 출력 메서드
    @Override
    public Page<PointLogsDTO> pointLogsList(Long userId, Pageable pageable) {

        return pointLogsRepository.pointLogsList(userId, pageable);
    }


    //사용자 포인트양 출력 메서드
    @Override
    public Integer getUserPointAmount(Long userId) {

        return pointRepository.getUserPointAmount(userId);
    }


}
