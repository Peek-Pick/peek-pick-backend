package org.beep.sbpp.reviews.service;

import org.beep.sbpp.reviews.enums.SentimentType;

public interface ReviewSentimentService {

    float testAnalyzeSentiment(String text) throws Exception;

//    ReviewSentimentResult analyze(String content);
//
//    record ReviewSentimentResult(SentimentType type, float score) {}
}
