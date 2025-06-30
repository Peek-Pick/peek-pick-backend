package org.beep.sbpp.summary.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.summary.client.GptClient;
import org.beep.sbpp.summary.dto.ReviewSummaryResponseDTO;
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

    @Override
    public void summarizeReviews() {
        // 감정 분석된 리뷰에서 상품 ID 목록을 가져옴
        List<Long> productIds = sentimentRepository.findDistinctProductIds();

        // 각 상품에 대해 긍정 및 부정 리뷰 요약 실행
        for (Long productId : productIds) {
            summarizeBySentiment(productId, SentimentType.valueOf("POSITIVE"));
            summarizeBySentiment(productId, SentimentType.valueOf("NEGATIVE"));
        }
    }

    /**
     * 특정 상품과 감정 유형(긍정/부정)에 대해 요약 실행
     */
    @Override
    public void summarizeBySentiment(Long productId, SentimentType sentiment) {
        // 해당 상품과 감정에 해당하는 리뷰 댓글 목록을 조회
        List<String> comments = sentimentRepository.findCommentsByProductIdAndSentiment(productId, sentiment);

        // 리뷰가 없으면 로깅 후 중단
        if (comments == null || comments.isEmpty()) {
            log.info("No comments found for productId {} and sentiment {}", productId, sentiment);
            return;
        }

        // GPT 요청용 프롬프트 생성
        String prompt = buildPrompt(sentiment, comments);

        // GPT로부터 요약 결과를 받아옴
        String summaryText = gptClient.getSummary(prompt);

        // 요약 결과를 DB에 저장 또는 업데이트
        summaryRepository.saveOrUpdate(productId, sentiment, summaryText, comments.size());
    }

    /**
     * GPT에게 요청할 요약 프롬프트를 생성
     */
    @Override
    public String buildPrompt(SentimentType sentiment, List<String> comments) {
        // 프롬프트 서두: 감정 유형에 따라 안내 문구 다르게 구성
        String intro = sentiment.equals(SentimentType.POSITIVE) ?
                "다음은 한 상품에 대한 긍정 리뷰입니다. 주요 공통 의견을 영어 한 문장 이내로 요약해 주세요.\n\n" :
                "다음은 한 상품에 대한 부정 리뷰입니다. 주요 공통 의견을 영어 한 문장 이내로 요약해 주세요.\n\n";

        // 리뷰 100개까지만 사용하며, 보기 좋게 리스트 형태로 연결
        String joined = comments.stream()
                .limit(100) // 토큰 절약을 위해 최대 100개까지 제한
                .collect(Collectors.joining("\n- ", "- ", ""));

        // 최종 프롬프트 구성
        return intro + joined;
    }

    @Override
    public ReviewSummaryResponseDTO getSummaryByProductId(Long productId) {
        // Optional로 받기
        var positiveOpt = summaryRepository.findByProductEntity_productIdAndSentiment(productId, SentimentType.POSITIVE);
        var negativeOpt = summaryRepository.findByProductEntity_productIdAndSentiment(productId, SentimentType.NEGATIVE);

        var positive = positiveOpt.orElse(null);
        var negative = negativeOpt.orElse(null);

        int total = (positive != null ? positive.getReviewCount() : 0)
                + (negative != null ? negative.getReviewCount() : 0);

        int percent = total == 0 ? 0 :
                Math.round((positive != null ? (positive.getReviewCount() * 100.0f / total) : 0));

        return new ReviewSummaryResponseDTO(
                productId,
                percent,
                positive != null ? positive.getSummaryText() : "No positive reviews",
                negative != null ? negative.getSummaryText() : "No negative reviews"
        );
    }

}
