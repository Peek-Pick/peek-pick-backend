package org.beep.sbpp.admin.dashboard.service;

import org.beep.sbpp.admin.dashboard.dto.AdminDashboardInquiryDTO;
import org.beep.sbpp.admin.dashboard.dto.AdminDashboardReportDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface AdminDashboardService {
    Page<AdminDashboardReportDTO> getAdminDashboardReportList(Pageable pageable);

    Page<AdminDashboardInquiryDTO> getAdminDashboardInquiryList(Pageable pageable);

    List<Map<String, Object>> getAdminDashboardChartReview();

    List<Map<String, Object>> getAdminDashboardChartUser();

    List<Map<String, Object>> getAdminDashboardChartNationality();

    List<List<Object>> getAdminDashboardStatus();
}