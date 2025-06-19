package org.beep.sbpp.summary.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.summary.client.GptClient;
import org.beep.sbpp.summary.enums.SentimentType;
import org.beep.sbpp.summary.repository.ProductReviewSummaryRepository;
import org.beep.sbpp.summary.repository.ReviewSentimentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ReviewSummaryServiceImpl implements ReviewSummaryService {

    private final ReviewSentimentRepository sentimentRepository;
    private final ProductReviewSummaryRepository summaryRepository;
    private final GptClient gptClient;

    public void summarizeReviews() {
        List<Long> productIds = sentimentRepository.findDistinctProductIds();

        for (Long productId : productIds) {
            summarizeBySentiment(productId, SentimentType.valueOf("POSITIVE"));
            summarizeBySentiment(productId, SentimentType.valueOf("NEGATIVE"));
        }
    }

    @Override
    public void summarizeBySentiment(Long productId, SentimentType sentiment) {
        List<String> comments = sentimentRepository.findCommentsByProductIdAndSentiment(productId, sentiment);

        if (comments == null || comments.isEmpty()) {
            log.info("No comments found for productId {} and sentiment {}", productId, sentiment);
            return;
        }

        String prompt = buildPrompt(sentiment, comments);
        String summaryText = gptClient.getSummary(prompt);

        summaryRepository.saveOrUpdate(productId, sentiment, summaryText, comments.size());
    }

    @Override
    public String buildPrompt(SentimentType sentiment, List<String> comments) {
        String intro = sentiment.equals("POSITIVE") ?
                "다음은 한 상품에 대한 긍정 리뷰입니다. 주요 공통 의견을 한국어 한 문장으로 요약해 주세요.\n\n" :
                "다음은 한 상품에 대한 부정 리뷰입니다. 주요 불만 사항을 한국어 한 문장으로 요약해 주세요.\n\n";

        String joined = comments.stream()
                .limit(100) // 최대 100개 제한 (토큰 절약)
                .collect(Collectors.joining("\n- ", "- ", ""));

        return intro + joined;
    }
}
