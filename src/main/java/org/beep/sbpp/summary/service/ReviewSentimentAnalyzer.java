package org.beep.sbpp.summary.service;

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.summary.enums.SentimentType;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewSentimentAnalyzer {

    private final LanguageServiceClient languageClient;

    public ReviewSentimentResult analyze(String content) throws Exception {
        Document doc = Document.newBuilder()
                .setContent(content)
                .setType(Document.Type.PLAIN_TEXT)
                .build();

        Sentiment sentiment = languageClient.analyzeSentiment(doc).getDocumentSentiment();

        float score = sentiment.getScore();

        SentimentType type;
        if (score >= 0.3) {
            type = SentimentType.POSITIVE;
        } else if (score <= -0.3) {
            type = SentimentType.NEGATIVE;
        } else {
            type = SentimentType.NEUTRAL;
        }

        return new ReviewSentimentResult(type, score);
    }

    public record ReviewSentimentResult(SentimentType type, float score) {}
}
