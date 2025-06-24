package org.beep.sbpp.chatbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ChatbotServiceImpl implements ChatbotService {

    VectorStore vectorStore;
    private final ChatClient.Builder builder;

    OpenAiChatOptions defaultOptions = OpenAiChatOptions.builder()
            .model("gpt-3.5-turbo")   // 또는 "gpt-4"
            .temperature(0.5)
            .build();

    public ChatbotServiceImpl(VectorStore vectorStore, ChatClient.Builder builder) {

        this.vectorStore = vectorStore;
        this.builder = builder;
    }

    public ChatClient createChatClient() {
        InMemoryChatMemory memory = new InMemoryChatMemory(); // 새 메모리 생성
        return builder
                .defaultOptions(defaultOptions) // 기본 옵션
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(memory)
                        //, new QuestionAnswerAdvisor(vectorStore)
                )
                .build();
    }


    // 질문 분류 매칭
    @Override
    public String handleUserQuery(String userQuery) {
        ChatClient chatClient = createChatClient();
        String category = classifyQuestion(userQuery);
        log.info("🧠 분류 결과: '{}'", category);
        log.info("userquery: {}", userQuery);
        return switch (category) {
            case "1" -> productRecommend(userQuery);
            case "2" -> "서비스 관련 기능 준비 중입니다."; // 또는 서비스 처리
            case "3" -> {
                String prompt = """
                    아래 질문에 답변해줘:
                    질문: {input}
                    답변:
                    """;
                PromptTemplate template = new PromptTemplate(prompt);
                Map<String, Object> params = Map.of("input", userQuery);
                yield chatClient.prompt(template.create(params)).call().content();
            }
            default -> "질문을 이해하지 못했어요.";
        };
    }



    @Override
    public String classifyQuestion(String userQuestion) {
        ChatClient chatClient = createChatClient();
        String prompt = """
            다음 질문을 세 가지 중 하나로 분류해줘:
            1. 상품 추천 (예: "비슷한 상품 추천해줘", "상큼한 과일 주스 추천")
            2. 우리 서비스 관련 질문 (예: 로그인 문제, 바코드 인식, 리뷰 작성, 계정 설정, 포인트 사용 등)
            3. 그 외의 인사, 잡담 또는 일반 지식 관련 질문 (예: "안녕", "안녕하세요", "반가워", "오늘 날씨 어때?")
            
            질문: {input}
            
            위 세 가지 분류 중 하나에 해당하는 숫자(1, 2, 3) 정확히 하나만 출력해줘.
            마침표나 다른 문자는 출력하지마.
            """;

        PromptTemplate template = new PromptTemplate(prompt);
        Map<String, Object> params = Map.of("input", userQuestion);

        return chatClient.prompt(template.create(params)).call().content().trim();
    }

    // 벡터 기반 유사도 검색 → 유저 입력과 가장 유사한 상품 설명 5개 추출
    public String getSimilarProduct(String query) {
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(5).build()
        );

        return documents.stream()
                .map(doc -> {
                    String content = doc.getFormattedContent(); // 상품 설명
                    Map<String, Object> metadata = doc.getMetadata();
                    String barcode = (String) metadata.getOrDefault("barcode", "없음");
                    String imgUrl = (String) metadata.getOrDefault("imgUrl", "없음");

                    return String.format("""
                설명: %s
                바코드: %s
                이미지 url: %s
                """, content, barcode, imgUrl);
                })
                .collect(Collectors.joining("\n"));
    }


    // 유저의 질문 기반으로 상품 추천 → vectorStore에서 유사한 상품 설명 검색 후 ChatClient로 응답 생성
    @Override
    public String productRecommend(String userQuery) {
        ChatClient chatClient = createChatClient();
        String prompt = """
            너는 상품 추천 AI야.
            아래 상품 설명들을 참고해서 사용자의 질문에 가장 적합한 상품을 하나 추천해줘.
       
            반드시 아래 형식의 JSON으로만 응답해줘(추천 이유는 ~입니다 형태의 문장형으로 작성해줘). 이 형식 외에 다른 말은 하지마
            {{
              "productName": "추천 상품 이름",
              "reason": "추천 이유",
              "productId": "상품 고유 ID",
              "barcode": "상품 바코드",
              "imgUrl": "이미지 url"
            }}
    
            상품 설명들:
            {documents}
    
            질문: {x}
    
            응답:
            """;

        // 프롬프트에 넣을 파라미터 구성
        Map<String, Object> params = new HashMap<>();
        params.put("x", userQuery);
        params.put("documents", getSimilarProduct(userQuery));


        // ChatClient로 프롬프트 실행
        PromptTemplate template = new PromptTemplate(prompt);
        return chatClient.prompt(template.create(params)).call().content();
    }






}
