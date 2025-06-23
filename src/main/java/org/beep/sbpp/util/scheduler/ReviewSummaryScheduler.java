package org.beep.sbpp.util.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.summary.service.ReviewSummaryService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewSummaryScheduler {

    private final ReviewSummaryService reviewSummaryService;

//    @Scheduled(cron = "0 0 3 * * *") //매일 새벽 3시
    @Scheduled(cron = "0 27 * * * *")
    public void runSummaryJob() {
        log.info("🕒 리뷰 요약 배치 시작");
        try {
            reviewSummaryService.summarizeReviews();
            log.info("✅ 리뷰 요약 배치 완료");
        } catch (Exception e) {
            log.error("❌ 리뷰 요약 배치 실패: {}", e.getMessage(), e);
        }
    }
}

