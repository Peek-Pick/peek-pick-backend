package org.beep.sbpp.admin.reviews.repository;

import org.beep.sbpp.reviews.entities.ReviewReportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminReviewReportRepositoryCustom {
    Page<ReviewReportEntity> findAllWithFilterAndSort(Pageable pageable, String category, String keyword);
}