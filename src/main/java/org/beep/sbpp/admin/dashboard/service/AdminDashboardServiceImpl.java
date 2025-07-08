package org.beep.sbpp.admin.dashboard.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.admin.dashboard.dto.AdminDashboardInquiryDTO;
import org.beep.sbpp.admin.dashboard.dto.AdminDashboardReportDTO;
import org.beep.sbpp.admin.dashboard.repository.AdminDashboardInquiryRepository;
import org.beep.sbpp.admin.products.repository.AdminProductRepository;
import org.beep.sbpp.admin.reviews.repository.AdminReviewReportRepository;
import org.beep.sbpp.admin.reviews.repository.AdminReviewRepository;
import org.beep.sbpp.admin.users.repository.AdminUserRepository;
import org.beep.sbpp.inquiries.entities.Inquiry;

import org.beep.sbpp.reviews.entities.ReviewReportEntity;
import org.beep.sbpp.users.entities.UserProfileEntity;
import org.beep.sbpp.users.repository.UserProfileRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {
    private final AdminReviewReportRepository adminReviewReportRepository;
    private final AdminDashboardInquiryRepository adminDashInquiryRepository;
    private final AdminReviewRepository adminReviewRepository;
    private final AdminUserRepository adminUserRepository;
    private final UserProfileRepository userProfileRepository;
    private final AdminProductRepository adminproductRepository;

    @Override
    public Page<AdminDashboardReportDTO> getAdminDashboardReportList(Pageable pageable) {
        // pageable - regDate 기준 최신순 정렬
        Page<ReviewReportEntity> page = adminReviewReportRepository.findAll(pageable);

        return page.map(report -> {
            // 닉네임, 프로필 조회
            UserProfileEntity userProfileEntity = userProfileRepository.findByUserId(report.getUserEntity().getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("No data found to get. userId: " + report.getUserEntity().getUserId()));

            // 빌더로 DTO 생성
            AdminDashboardReportDTO.AdminDashboardReportDTOBuilder builder = AdminDashboardReportDTO.builder()
                    .reviewReportId(report.getReviewReportId())
                    .reviewId(report.getReviewEntity().getReviewId())
                    .reviewerId(report.getReviewEntity().getUserEntity().getUserId())
                    .nickname(userProfileEntity.getNickname())
                    .reason(report.getReason())
                    .regDate(report.getRegDate());

            return builder.build();
        });
    }

    @Override
    public Page<AdminDashboardInquiryDTO> getAdminDashboardInquiryList(Pageable pageable) {
        // pageable - regDate 기준 최신순 정렬
        Page<Inquiry> page = adminDashInquiryRepository.findAll(pageable);

        return page.map(inquiry -> {
            // 닉네임, 프로필 조회
            UserProfileEntity userProfileEntity = userProfileRepository.findByUserId(inquiry.getUserEntity().getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("No data found to get. userId: " + inquiry.getUserEntity().getUserId()));

            // 빌더로 DTO 생성
            AdminDashboardInquiryDTO.AdminDashboardInquiryDTOBuilder builder = AdminDashboardInquiryDTO.builder()
                    .inquiryId(inquiry.getInquiryId())
                    .userId(inquiry.getUserEntity().getUserId())
                    .nickname(userProfileEntity.getNickname())
                    .content(inquiry.getContent())
                    .type(inquiry.getType())
                    .status(inquiry.getStatus())
                    .regDate(inquiry.getRegDate());

            return builder.build();
        });
    }

    @Override
    public List<Map<String, Object>> getAdminDashboardChartReview() {
        // 시작일, 종료일 계산
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(5).withDayOfMonth(1);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        // 최근 6개월 리뷰 추이 데이터 리스트
        return adminReviewRepository.countMonthlyReviews(startDateTime, endDateTime).stream().map(row -> {
            Map<String, Object> map = new HashMap<>();

            map.put("x", row[0] + "월");
            map.put("y", row[1]);

            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getAdminDashboardChartUser() {
        // 시작일, 종료일 계산
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusMonths(5).withDayOfMonth(1);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        // 최근 6개월 가입자 추이 데이터 리스트
        return adminUserRepository.countMonthlyJoinUsers(startDateTime, endDateTime).stream().map(row -> {
            Map<String, Object> map = new HashMap<>();

            map.put("x", row[0] + "월");
            map.put("y", row[1]);

            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<Map<String, Object>> getAdminDashboardChartNationality() {
        // 국적별 회원수 리스트
        return adminUserRepository.countUsersByNationality().stream().map(row -> {
            Map<String, Object> map = new HashMap<>();

            map.put("x", row[0]);
            map.put("y", row[1]);

            return map;
        }).collect(Collectors.toList());
    }

    @Override
    public List<List<Object>> getAdminDashboardStatus() {
        // 이번달 데이터
        LocalDate now = LocalDate.now();
        int month = now.getMonthValue();
        int year = now.getYear();

        log.info("--------------------------------------");

        Long productCount = adminproductRepository.countByMonth(month, year);
        Long userCount = adminUserRepository.countByMonth(month, year);
        Long reviewCount = adminReviewRepository.countByMonth(month, year);

        // 저번달 데이터
        LocalDate prevMonthDate = now.minusMonths(1);
        int prevMonth = prevMonthDate.getMonthValue();
        int prevYear = prevMonthDate.getYear();

        Long productPrevCount = adminproductRepository.countByMonth(prevMonth, prevYear);
        Long userPrevCount = adminUserRepository.countByMonth(prevMonth, prevYear);
        Long reviewPrevCount = adminReviewRepository.countByMonth(prevMonth, prevYear);

        // 데이터 증감율 계산하기
        double percentProduct = calculatePercentChange(productCount, productPrevCount);
        double percentUser = calculatePercentChange(userCount, userPrevCount);
        double percentReview = calculatePercentChange(reviewCount, reviewPrevCount);

        // 리스트 구성후 반환하기
        List<Object> values = List.of(productCount, userCount, reviewCount);
        List<Object> percents = List.of(percentProduct, percentUser, percentReview);

        log.info(values.toString());
        log.info(percents.toString());

        return List.of(values, percents);
    }

    private double calculatePercentChange(Long current, Long previous) {
        if (previous == 0) {
            return current > 0 ? 100.0 : 0.0;
        }
        return ((double)(current - previous) / previous) * 100.0;
    }
}