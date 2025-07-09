package org.beep.sbpp.chatbot.service;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.chatbot.util.LimitedInMemoryChatMemory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
@Slf4j
public class ChatbotServiceImpl implements ChatbotService {

    VectorStore vectorStore;
    private final ChatClient.Builder builder;

    OpenAiChatOptions defaultOptions = OpenAiChatOptions.builder()
            .model("gpt-4o")   // "gpt-4o" 또는 "gpt-3.5-turbo
            .maxTokens(500)    // 응답 토큰 길이 제한
            .temperature(0.5)
            .build();


    public ChatbotServiceImpl(VectorStore vectorStore, ChatClient.Builder builder) {
        this.vectorStore = vectorStore;
        this.builder = builder;
    }


    private static final String MEMORY_SESSION_KEY = "chat_memory";

    // 세션 메모리 기반 ChatClient 생성
    public ChatClient createChatClient(HttpSession session) {

        // 세션에서 메모리 가져오기
        LimitedInMemoryChatMemory memory = (LimitedInMemoryChatMemory) session.getAttribute(MEMORY_SESSION_KEY);
        // 메모리가 없으면 새로 생성 후 세션에 저장
        if (memory == null) {
            memory = new LimitedInMemoryChatMemory(4); // 최근 4개 메시지만 Sliding Window로 유지
            session.setAttribute(MEMORY_SESSION_KEY, memory);
        }

        return builder
                .defaultOptions(defaultOptions) // 기본 옵션
                //.defaultAdvisors(new MessageChatMemoryAdvisor(memory)) // 메시지를 memory에 자동 저장
                .build();
    }

    // 메모리 없이 ChatClient 생성
    public ChatClient createStatelessChatClient() {
        return builder
                .defaultOptions(defaultOptions)
                .build();
    }

    // 질문 분류 매칭
    @Override
    public String handleUserQuery(String userQuery, HttpSession session) {
        String category = classifyQuestion(userQuery);
        log.info("🧠 분류 결과: '{}'", category);
        log.info("userquery: {}", userQuery);

        return switch (category) {
            case "1" -> productRecommend(userQuery, session);
            case "2" -> faqAnswer(userQuery, session);
            case "3" -> generalAnswer(userQuery, session);
            default -> "질문을 이해하지 못했어요.";
        };
    }

    private String classifyQuestion(String userQuestion) {
        ChatClient chatClient = createStatelessChatClient(); // 메모리에 안쌓이게 - 최근 대화 내역에 추가되면 안되니까
        String prompt = """
            다음 질문을 세 가지 중 하나로 분류해줘:
            1. 상품 추천 (예: "상큼한 과일 주스 추천", "상품 정보좀")
            2. 우리 서비스 관련 질문 (예: 바코드 인식, 리뷰 작성, 계정 설정, 포인트 사용 등)
            3. 그 외의 인사, 잡담 또는 일반 지식 관련 질문 (예: "안녕", "반가워", "오늘 날씨 어때?")
            
            질문: {input}
            
            위 세 가지 분류 중 하나에 해당하는 숫자(1, 2, 3) 정확히 하나만 출력해줘.
            숫자만 출력해야해. 마침표나 다른 문자는 절대 출력하지마.
            """;

        PromptTemplate template = new PromptTemplate(prompt);
        Map<String, Object> params = Map.of("input", userQuestion);

        return chatClient.prompt(template.create(params)).call().content().trim();
    }

    // 벡터 기반 유사도 검색 → 유저 입력과 가장 유사한 설명 5개 추출
    private String getSimilar(String query, String type) {
        // 유사도 검색 요청
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(3).build()  // 유사도 검색 시 상위 3개만 추출
        );

        // 포맷팅
        return documents.stream()
                .filter(doc -> type.equals(doc.getMetadata().get("type")))
                .limit(2)  // 3개중 2개만 GPT 프롬프트에 사용
                .map(doc -> formatByType(doc, type)) // ← 타입별 포맷 분기
                .collect(Collectors.joining("\n"));
    }

    // 타입별 포맷 분기
    private String formatByType(Document doc, String type) {
        String content = doc.getFormattedContent();
        Map<String, Object> metadata = doc.getMetadata();

        return switch (type) {
            case "product" -> {
                String barcode = (String) metadata.getOrDefault("barcode", "없음");
                String imgUrl = (String) metadata.getOrDefault("imgUrl", "없음");
                yield String.format("""
                설명: %s
                바코드: %s
                이미지 url: %s
                """, content, barcode, imgUrl);
            }
            case "faq" -> content;
            default -> "지원하지 않는 타입입니다.";
        };
    }

    // 1. 유저의 질문 기반으로 상품 추천 → vectorStore에서 유사한 상품 설명 검색 후 ChatClient로 응답 생성
    private String productRecommend(String userQuery, HttpSession session) {
        ChatClient chatClient = createChatClient(session);

        // 최근 대화 내용 가져오기
        LimitedInMemoryChatMemory memory = (LimitedInMemoryChatMemory) session.getAttribute(MEMORY_SESSION_KEY);
        memory.add("default", List.of(new UserMessage(userQuery)));
        String recentMessagesStr = getRecentMessagesAsString(memory);

        String prompt = """
            너는 상품 추천 챗봇AI야
            아래 상품 설명들을 참고해서 질문에 가장 적합한 상품을 하나 추천해줘(추천 이유는 문장형으로 작성).
            사용자가 질문한 언어를 감지해서, 꼭 질문한 언어로 답변해줘. 상품 이름도 번역해줘.
            
            반드시 아래 형식의 JSON으로만 응답해줘,
            이 형식 외에 다른 말은 하지마.
            절대 코드블록(예: ```json)으로 감싸지 말고,
            오직 중괄호(예: {{ 로 시작하여 }} 로 끝나는) JSON 텍스트만 그대로 출력해.
            {{
              "productName": "상품 이름",
              "reason": "추천 이유",
              "barcode": "상품 바코드",
              "imgUrl": "이미지 url"
            }}
            
            최근 대화 내용:
            {recentMessages}
        
            상품 설명들:
            {documents}
    
            질문: {x}
    
            응답:
            """;

        // 프롬프트에 넣을 파라미터 구성
        Map<String, Object> params = new HashMap<>();
        params.put("x", userQuery);
        params.put("documents", getSimilar(userQuery, "product"));
        params.put("recentMessages", recentMessagesStr);

        // ChatClient로 프롬프트 실행
        PromptTemplate template = new PromptTemplate(prompt);

        // AI 응답 생성
        String response = chatClient.prompt(template.create(params)).call().content();

        // 챗봇 답변을 memory에 저장
        memory.add("default", List.of(new AssistantMessage(response)));

        return response;
    }


    // 2. FAQ 답변
    public String faqAnswer(String userQuery, HttpSession session) {
        ChatClient chatClient = createChatClient(session);

        // 최근 대화 내용 가져오기
        LimitedInMemoryChatMemory memory = (LimitedInMemoryChatMemory) session.getAttribute(MEMORY_SESSION_KEY);
        memory.add("default", List.of(new UserMessage(userQuery)));
        String recentMessagesStr = getRecentMessagesAsString(memory);

        String prompt = """
        다음은 FAQ 문서야. 이걸 참고해서 질문에 가장 적절한 답변을 해줘.
        사용자가 질문한 언어를 감지해서, 질문한 언어로 답변해줘.

        최근 대화 내용:
        {recentMessages}
        
        FAQ 문서:
        {documents}

        질문: {x}

        응답:
        """;

        // 프롬프트에 넣을 파라미터 구성
        Map<String, Object> params = new HashMap<>();
        params.put("x", userQuery);
        params.put("documents", getSimilar(userQuery, "faq"));
        params.put("recentMessages", recentMessagesStr);

        // ChatClient로 프롬프트 실행
        PromptTemplate template = new PromptTemplate(prompt);

        // AI 응답 생성
        String response = chatClient.prompt(template.create(params)).call().content();

        // 챗봇 답변을 memory에 저장
        memory.add("default", List.of(new AssistantMessage(response)));

        return response;
    }

    // 3. 일반 질문 답변
    private String generalAnswer(String userQuery, HttpSession session) {
        ChatClient chatClient = createChatClient(session);

        // memory 꺼내기
        LimitedInMemoryChatMemory memory = (LimitedInMemoryChatMemory) session.getAttribute(MEMORY_SESSION_KEY);

        // user 메시지 직접 memory에 저장
        memory.add("default", List.of(new UserMessage(userQuery)));

        // 최근 채팅 가져오기
        String recentMessagesStr = getRecentMessagesAsString(memory);

        String prompt = """
        너는 AI 챗봇이야.
        
        최근 대화 내용:
        {recentMessages}
        
        이 내용을 참고해 사용자의 질문에 답해줘.
        (사용자가 질문한 언어를 감지해서, 질문한 언어로 답해줘.)
        
        질문: {input}
        
        응답:
        """;
        PromptTemplate template = new PromptTemplate(prompt);
        Map<String, Object> params = Map.of(
                "input", userQuery,
                "recentMessages", recentMessagesStr
        );
        // AI 응답 생성
        String response = chatClient.prompt(template.create(params)).call().content();

        // 챗봇 답변을 memory에 저장
        memory.add("default", List.of(new AssistantMessage(response)));

        return response;
    }

    // 최근 채팅 내용 추출
    private String getRecentMessagesAsString(LimitedInMemoryChatMemory memory) {
        List<Message> recentMessages = memory.get("default", 4);
        StringBuilder sb = new StringBuilder();

        // recentMessages를 문자열로 변환
        for (Message msg : recentMessages) {
            String role = msg.getClass().getSimpleName().replace("Message", "");
            String content = extractContentFromToString(msg);
            sb.append(role).append(": ").append(content).append("\n");
        }

        String recentMessagesStr = sb.toString();
        System.out.println("💡 최근 메시지: " + recentMessagesStr);

        return recentMessagesStr;


    }

    // toString() 결과에서 내용만 추출
    private String extractContentFromToString(Message msg) {
        String dump = msg.toString();
        // AssistantMessage [messageType=ASSISTANT, toolCalls=[], textContent=3, metadata={...}]
        Pattern p = Pattern.compile("textContent=(.*?),");
        Matcher m = p.matcher(dump);
        if (m.find()) {
            return m.group(1);
        }
        // UserMessage{content='안녕', ...}
        p = Pattern.compile("content='(.*?)'");
        m = p.matcher(dump);
        if (m.find()) {
            return m.group(1);
        }
        return dump; // fallback
    }



}
