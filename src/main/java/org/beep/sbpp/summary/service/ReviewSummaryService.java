package org.beep.sbpp.summary.service;

import org.beep.sbpp.summary.enums.SentimentType;

import java.util.List;

public interface ReviewSummaryService {

    void summarizeReviews();

    void summarizeBySentiment(Long productId, SentimentType sentiment);

    String buildPrompt(SentimentType sentiment, List<String> comments);
}
