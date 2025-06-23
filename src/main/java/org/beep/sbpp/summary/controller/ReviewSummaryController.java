package org.beep.sbpp.summary.controller;

import lombok.RequiredArgsConstructor;
import org.beep.sbpp.summary.dto.ReviewSummaryResponseDTO;
import org.beep.sbpp.summary.enums.SentimentType;
import org.beep.sbpp.summary.service.ReviewSentimentService;
import org.beep.sbpp.summary.service.ReviewSummaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews/")
@RequiredArgsConstructor
public class ReviewSummaryController {

    private final ReviewSentimentService sentimentService;

    private final ReviewSummaryService reviewSummaryService;

    // (taland 테스트용 - 감성분석)
    @GetMapping("/sentiment")
    public ResponseEntity<Float> analyze(@RequestParam String text) throws Exception {
        float score = sentimentService.testAnalyzeSentiment(text);
        return ResponseEntity.ok(score);
    }

    // 리뷰 요약 보내기
    @GetMapping("/summary/{productId}")
    public ReviewSummaryResponseDTO getReviewSummary(@PathVariable Long productId) {
        return reviewSummaryService.getSummaryByProductId(productId);
    }

    // 특정 상품 ID에 대해 긍정/부정 요약을 실행(taland 테스트용)
    @PostMapping("/summary/{productId}")
    public String testSummarize(@PathVariable Long productId) {
        reviewSummaryService.summarizeBySentiment(productId, SentimentType.POSITIVE);
        reviewSummaryService.summarizeBySentiment(productId, SentimentType.NEGATIVE);

        return "요약 완료 for productId: " + productId;
    }


}
