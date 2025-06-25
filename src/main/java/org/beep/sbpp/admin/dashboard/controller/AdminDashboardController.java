package org.beep.sbpp.admin.dashboard.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.admin.dashboard.dto.AdminDashboardInquiryDTO;
import org.beep.sbpp.admin.dashboard.dto.AdminDashboardReportDTO;
import org.beep.sbpp.admin.dashboard.service.AdminDashboardService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {
    private final AdminDashboardService adminDashboardService;

    // 대시보드 신고 리스트
    @GetMapping("/report")
    public ResponseEntity<Page<AdminDashboardReportDTO>> AdminDashboardReport (
            @PageableDefault(size = 3, sort = "regDate", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(adminDashboardService.getAdminDashboardReportList(pageable));
    }

    // 대시보드 문의 리스트
    @GetMapping("/inquiry")
    public ResponseEntity<Page<AdminDashboardInquiryDTO>> AdminDashboardInquiry (
            @PageableDefault(size = 3, sort = "regDate", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(adminDashboardService.getAdminDashboardInquiryList(pageable));
    }

    // 통계 차트 - 리뷰 추이
    @GetMapping("/chart/review")
    public ResponseEntity<List<Map<String, Object>>> AdminDashboardChartReview() {
        return ResponseEntity.ok(adminDashboardService.getAdminDashboardChartReview());
    }

    // 통계 차트 - 사용자 추이
    @GetMapping("/chart/user")
    public ResponseEntity<List<Map<String, Object>>> AdminDashboardChartUser() {
        return ResponseEntity.ok(adminDashboardService.getAdminDashboardChartUser());
    }

    // 통계 차트 - 국적 분포
    @GetMapping("/chart/nationality")
    public ResponseEntity<List<Map<String, Object>>> AdminDashboardChartNationality() {
        return ResponseEntity.ok(adminDashboardService.getAdminDashboardChartNationality());
    }

    // 이번달 데이터
    @GetMapping("/status")
    public ResponseEntity<List<List<Object>>> AdminDashboardStatus() {
        return ResponseEntity.ok(adminDashboardService.getAdminDashboardStatus());
    }
}