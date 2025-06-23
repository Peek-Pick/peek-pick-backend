package org.beep.sbpp.summary.service;

import com.google.cloud.language.v1.*;
import com.google.cloud.language.v1.Document.Type;
import org.springframework.stereotype.Service;

@Service
public class ReviewSentimentServiceImpl implements ReviewSentimentService {

    // 테스트 감정분석(score 출력용)
    @Override
    public float testAnalyzeSentiment(String text) throws Exception {
        try (LanguageServiceClient language = LanguageServiceClient.create()) {
            Document doc = Document.newBuilder()
                    .setContent(text)
                    .setType(Type.PLAIN_TEXT)
                    .build();

            Sentiment sentiment = language.analyzeSentiment(doc).getDocumentSentiment();
            return sentiment.getScore();
        }
    }

}
