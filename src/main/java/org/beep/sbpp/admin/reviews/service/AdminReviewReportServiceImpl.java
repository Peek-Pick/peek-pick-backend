package org.beep.sbpp.admin.reviews.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.admin.reviews.dto.AdminReviewReportDTO;
import org.beep.sbpp.admin.reviews.repository.AdminReviewReportRepository;
import org.beep.sbpp.reviews.entities.ReviewReportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminReviewReportServiceImpl implements AdminReviewReportService {
    private final AdminReviewReportRepository adminReviewReportRepository;

    @Override
    public Page<AdminReviewReportDTO> getReviewReportList(Pageable pageable, String category, String keyword) {
        // pageable - regDate 기준 최신순 정렬, category, keyword - 필터링 기준
        Page<ReviewReportEntity> page =
                adminReviewReportRepository.findAllWithFilterAndSort(pageable, category, keyword);

        return page.map(report -> {
            // 빌더로 DTO 생성
            AdminReviewReportDTO.AdminReviewReportDTOBuilder builder = AdminReviewReportDTO.builder()
                    .reviewReportId(report.getReviewReportId())
                    .userId(report.getUserEntity().getUserId())
                    .reviewId(report.getReviewEntity().getReviewId())
                    .reviewerId(report.getReviewEntity().getUserEntity().getUserId())
                    .reason(report.getReason())
                    .regDate(report.getRegDate());

            return builder.build();
        });
    }
}