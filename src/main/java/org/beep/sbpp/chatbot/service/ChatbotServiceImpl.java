package org.beep.sbpp.chatbot.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
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
    ChatClient chatClient;

    public ChatbotServiceImpl(VectorStore vectorStore, ChatClient.Builder builder) {
        this.vectorStore = vectorStore;
        this.chatClient = builder
                .defaultAdvisors( new MessageChatMemoryAdvisor(new InMemoryChatMemory()) // 간단한 메모리 보존
                        ,new QuestionAnswerAdvisor(vectorStore)) // 벡터 기반 유사도 QA
                .build();
    }


    // 질문 분류 매칭
    public String handleUserQuery(String userQuery) {
        String category = classifyQuestion(userQuery);

        return switch (category) {
            case "상품 추천" -> productRecommend(userQuery);
            case "서비스 관련" -> "서비스 관련 기능 준비 중입니다."; // 또는 서비스 처리
            case "일반 지식" -> chatClient.prompt(userQuery).call().content();
            default -> "질문을 이해하지 못했어요.";
        };
    }

    @Override
    public String classifyQuestion(String userQuestion) {
        String prompt = """
            다음 질문을 세 가지 중 하나로 분류해 주세요:
            1. 상품 추천 (예: "비슷한 상품 추천해줘", "상큼한 과일 주스 추천")
            2. 우리 서비스 관련 질문 (예: 로그인 문제, 바코드 인식, 리뷰 작성, 계정 설정, 포인트 사용 등)
            3. 그 외의 잡담 또는 일반 지식 관련 질문
            
            질문: {input}
            
            위 세 가지 분류 중 하나에 해당하는 숫자(1, 2, 3)만 출력하세요.
            """;

        PromptTemplate template = new PromptTemplate(prompt);
        Map<String, Object> params = Map.of("input", userQuestion);

        return chatClient.prompt(template.create(params)).call().content();
    }

    // 벡터 기반 유사도 검색 → 유저 입력과 가장 유사한 상품 설명 5개 추출
    public String getSimilarProductDescriptions(String query) {
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(5).build()
        );

        return documents.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n"));
    }


    // 유저의 질문 기반으로 상품 추천 → vectorStore에서 유사한 상품 설명 검색 후 ChatClient로 응답 생성
    @Override
    public String productRecommend(String userQuery) {
        String prompt = """
            너는 상품 추천 AI야.
            아래 상품 설명들을 참고해서 사용자의 질문에 가장 적합한 상품을 하나 추천해줘.
            추천 상품 이름과 이유를 간단히 설명해줘.

            상품 설명들:
            {documents}

            질문: {x}

            추천:
            """;

        // 프롬프트에 넣을 파라미터 구성
        Map<String, Object> params = new HashMap<>();
        params.put("x", userQuery);
        params.put("documents", getSimilarProductDescriptions(userQuery));

        // ChatClient로 프롬프트 실행
        PromptTemplate template = new PromptTemplate(prompt);
        return chatClient.prompt(template.create(params)).call().content();
    }






}
