package org.beep.sbpp.review;

import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.reviews.dto.ReviewReportDTO;
import org.beep.sbpp.reviews.enums.ReportReason;
import org.beep.sbpp.reviews.service.ReviewReportService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class ReviewReportServiceTest {
    @Autowired
    private ReviewReportService service;

    @Test
    public void testRegisterReport() {
        ReviewReportDTO dto1 = ReviewReportDTO.builder()
                .reviewId(1L)
                .userId(1L)
                .reason(ReportReason.POLITICS)
                .build();

        ReviewReportDTO dto2 = ReviewReportDTO.builder()
                .reviewId(1L)
                .userId(2L)
                .reason(ReportReason.POLITICS)
                .build();

        Long reportId1 = service.registerReport(dto1);
        log.info("New report ID: {}", reportId1);

        Long reportId2 = service.registerReport(dto2);
        log.info("New report ID: {}", reportId2);
    }
}