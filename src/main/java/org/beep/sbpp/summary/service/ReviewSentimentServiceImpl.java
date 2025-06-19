package org.beep.sbpp.summary.service;

import com.google.cloud.language.v1.*;
import com.google.cloud.language.v1.Document.Type;
import org.springframework.stereotype.Service;

@Service
//@Transactional
//@RequiredArgsConstructor
public class ReviewSentimentServiceImpl implements ReviewSentimentService {

//    private final LanguageServiceClient languageClient;

//    // 생성자 초기화
//    public ReviewSentimentServiceImpl() throws IOException {
//        this.languageClient = initializeClientFromEnv();
//    }

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

//    @Override
//    public ReviewSentimentResult analyze(String content) {
//        Document doc = Document.newBuilder()
//                .setContent(content)
//                .setType(Document.Type.PLAIN_TEXT)
//                .build();
//
//        Sentiment sentiment = languageClient.analyzeSentiment(doc).getDocumentSentiment();
//        float score = sentiment.getScore();
//
//        SentimentType type;
//        if (score >= 0.25) {
//            type = SentimentType.POSITIVE;
//        } else if (score <= -0.25) {
//            type = SentimentType.NEGATIVE;
//        } else {
//            type = SentimentType.NEUTRAL;
//        }
//
//        return new ReviewSentimentResult(type, score);
//    }
//
//    public record ReviewSentimentResult(SentimentType type, float score) {}



//    // 리뷰 감정 분석: score + enum 반환
//    @Override
//    public ReviewSentimentResult analyze(String text) {
//
//        // 요청할 텍스트 문서
//        Document doc = Document.newBuilder()
//                .setContent(text)
//                .setType(Type.PLAIN_TEXT)
//                .build();
//
//        // 감정 분석 수행
//        AnalyzeSentimentResponse response = languageClient.analyzeSentiment(doc);
//        Sentiment sentiment = response.getDocumentSentiment();
//
//        // 점수 추출
//        float score = sentiment.getScore();
//
//        // 점수 기반으로 type 변환
//        SentimentType sentimentType = convertToSentimentType(score);
//
//        // 결과 반환
//        return new ReviewSentimentResult(sentimentType, score);
//    }
//
//    // 클라이언트 초기화: JSON 문자열을 임시 파일로 변환한 후 인증해야 함
//    private LanguageServiceClient initializeClientFromEnv() throws IOException {
//
//        // 환경변수에서 문자열 추출
//        String json = System.getenv("GCP_KEY_JSON");
//        if (json == null || json.isEmpty()) {
//            throw new IllegalStateException("환경변수 GCP_KEY_JSON이 설정되지 않았습니다.");
//        }
//
//        // JSON 문자열 Map으로 파싱
//        ObjectMapper mapper = new ObjectMapper();
//        Map<String, Object> jsonMap = mapper.readValue(json, Map.class);
//
//        // 임시 JSON 파일 생성 후 환경변수 내용 저장
//        File tempKeyFile = Files.createTempFile("gcp-key-", ".json").toFile();
//        mapper.writeValue(tempKeyFile, jsonMap);
//
//        // 해당 임시 파일을 기반으로 인증 객체 생성
//        InputStream credentialsStream = new FileInputStream(tempKeyFile);
//        ServiceAccountCredentials credentials = ServiceAccountCredentials.fromStream(credentialsStream);
//
//        // 인증정보 LanguageServiceClient에 세팅
//        LanguageServiceSettings settings = LanguageServiceSettings.newBuilder()
//                .setCredentialsProvider(() -> credentials)
//                .build();
//
//        return LanguageServiceClient.create(settings);
//    }
//
//    // 점수 기반으로 enum 타입 변환
//    // ~-0.25 : Negative
//    // -0.25 ~ 0.25 : Neutral
//    // 0.25 ~ ; Positive
//    private SentimentType convertToSentimentType(float score) {
//        if (score >= 0.25f) return SentimentType.POSITIVE;
//        else if (score <= -0.25f) return SentimentType.NEGATIVE;
//        else return SentimentType.NEUTRAL;
//    }
}
