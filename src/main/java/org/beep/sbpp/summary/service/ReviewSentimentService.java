package org.beep.sbpp.summary.service;

public interface ReviewSentimentService {

    float testAnalyzeSentiment(String text) throws Exception;

//    ReviewSentimentResult analyze(String content);
//
//    record ReviewSentimentResult(SentimentType type, float score) {}
}
