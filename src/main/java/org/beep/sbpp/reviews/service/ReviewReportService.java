package org.beep.sbpp.reviews.service;

import org.beep.sbpp.reviews.dto.ReviewReportDTO;

public interface ReviewReportService {
    Long registerReport(ReviewReportDTO dto);
}