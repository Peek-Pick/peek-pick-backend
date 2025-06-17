package org.beep.sbpp.admin.reviews.service;

import org.beep.sbpp.admin.reviews.dto.AdminReviewDetailDTO;
import org.beep.sbpp.admin.reviews.dto.AdminReviewSimpleDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminReviewService {
    Page<AdminReviewSimpleDTO> getReviewList(Pageable pageable, String category, String keyword, Boolean hidden);

    AdminReviewDetailDTO getReviewDetail(Long reviewId);

    Long toggleHiddenStatus(Long reviewId);
}