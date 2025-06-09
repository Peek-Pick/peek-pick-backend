package org.beep.sbpp.admin.reviews.repository;

import org.beep.sbpp.reviews.entities.ReviewReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminReviewReportRepository extends JpaRepository<ReviewReportEntity, Long>,
        AdminReviewReportRepositoryCustom{
}