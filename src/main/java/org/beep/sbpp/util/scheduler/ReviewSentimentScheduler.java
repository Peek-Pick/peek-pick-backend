package org.beep.sbpp.util.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.beep.sbpp.summary.entities.ReviewSentimentEntity;
import org.beep.sbpp.reviews.repository.ReviewRepository;
import org.beep.sbpp.summary.repository.ReviewSentimentRepository;
import org.beep.sbpp.summary.service.ReviewSentimentAnalyzer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewSentimentScheduler {

    private final ReviewRepository reviewRepository;
    private final ReviewSentimentRepository sentimentRepository;
    private final ReviewSentimentAnalyzer analyzer;

    @Scheduled(cron = "0 0 0 * * *")
    public void analyzeNewOrUpdatedReviews() {
        log.info("✅ 감정분석 스케줄러 작동: " + LocalDateTime.now());
        List<ReviewEntity> reviews = reviewRepository.findAll();

        for (ReviewEntity review : reviews) {
            try {
                Optional<ReviewSentimentEntity> existingOpt = sentimentRepository.findByReviewEntity_ReviewId(review.getReviewId());

                if (existingOpt.isEmpty() || review.getModDate().isAfter(existingOpt.get().getReviewUpdatedAt())) {
                    analyzeAndSave(review);
                }

            } catch (Exception e) {
                log.error("❌ 리뷰[{}] 감정 분석 실패", review.getReviewId(), e);
            }
        }
    }

    private void analyzeAndSave(ReviewEntity review) throws Exception {
        ReviewSentimentAnalyzer.ReviewSentimentResult result = analyzer.analyze(review.getComment());

        ReviewSentimentEntity entity = new ReviewSentimentEntity();
        entity.setReviewEntity(review);
        entity.setProductEntity(review.getProductEntity());
        entity.setComment(review.getComment());
        entity.setSentiment(result.type());
        entity.setScore(result.score());
        entity.setAnalyzedAt(LocalDateTime.now());
        entity.setReviewUpdatedAt(review.getModDate());

        sentimentRepository.save(entity);
        log.info("✅ 리뷰[{}] 분석 완료: {} (score={})", review.getReviewId(), result.type(), result.score());
    }
}
