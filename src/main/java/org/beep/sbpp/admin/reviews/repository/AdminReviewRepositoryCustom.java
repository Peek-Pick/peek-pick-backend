package org.beep.sbpp.admin.reviews.repository;

import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminReviewRepositoryCustom {
    Page<ReviewEntity> findAllWithFilterAndSort(Pageable pageable, String category, String keyword, Boolean hidden, String lang);
}