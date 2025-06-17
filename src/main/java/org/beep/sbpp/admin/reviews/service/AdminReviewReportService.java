package org.beep.sbpp.admin.reviews.service;

import org.beep.sbpp.admin.reviews.dto.AdminReviewReportDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminReviewReportService {
    Page<AdminReviewReportDTO> getReviewReportList(Pageable pageable, String category, String keyword, Boolean hidden);
}