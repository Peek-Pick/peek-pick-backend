package org.beep.sbpp.chatbot.service;

import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.chatbot.dto.ProductVectorDTO;
import org.beep.sbpp.chatbot.repository.ChatbotRepository;
import org.beep.sbpp.products.entities.ProductEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
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

    private final ChatbotRepository chatbotRepository;

    VectorStore vectorStore;
    ChatClient chatClient;

    public ChatbotServiceImpl(ChatbotRepository chatbotRepository, VectorStore vectorStore, ChatClient.Builder builder) {
        this.chatbotRepository = chatbotRepository;
        this.vectorStore = vectorStore;
        this.chatClient = builder
                .defaultAdvisors( new MessageChatMemoryAdvisor(new InMemoryChatMemory()) // 간단한 메모리 보존
                        ,new QuestionAnswerAdvisor(vectorStore)) // 벡터 기반 유사도 QA
                .build();
    }

    // 초기 DB 상품 전체 벡터화
    @Override
    public void vectorizeInit() {
        List<ProductEntity> products = chatbotRepository.findAll();

        // 상품을 벡터화
        List<Document> docs = products.stream()
                .map(p -> {
                    // metadata 구성: 상품ID, 이름, 카테고리, 태그
                    Map<String, Object> meta = new HashMap<>();
                    meta.put("productId", String.valueOf(p.getProductId()));
                    meta.put("name", p.getName() != null ? p.getName() : "알수없음");
                    meta.put("category", p.getCategory() != null ? p.getCategory() : "알수없음");
                    meta.put("mainTag", p.getMainTag() != null ? p.getMainTag() : "알수없음");
                    // Document 생성 (content: 상품명, 설명, 카테고리, 태그, 알레르기 정보 + 원재료, 영양성분 고려중)
                    return new Document(
                            "상품명: " + p.getName() + "\n" +
                            "설명: " + p.getDescription() + "\n" +
                            "카테고리: " + p.getCategory() + "\n" +
                            "태그: " + p.getMainTag() + "\n" +
                            "알레르기 정보: " + p.getAllergens(),
                            meta
                    );
                })

                // 문서를 토큰 기준으로 나누어(Chunking) 여러 개의 Document로 분할
                .flatMap(doc -> new TokenTextSplitter().apply(List.of(doc)).stream())
                .collect(Collectors.toList());

        vectorStore.add(docs);
    }

    // 단일 상품 등록 + 벡터화
    @Override
    public void addProduct(ProductVectorDTO dto) {
        // metadata
        Map<String, Object> meta = new HashMap<>();
        meta.put("productId", String.valueOf(dto.getProductId()));
        meta.put("name", dto.getName() != null ? dto.getName() : "알수없음");
        meta.put("category", dto.getCategory() != null ? dto.getCategory() : "알수없음");
        meta.put("mainTag", dto.getMainTag() != null ? dto.getMainTag() : "알수없음");
        // content
        Document doc = new Document(
                "상품명: " + dto.getName() + "\n" +
                "설명: " + dto.getDescription() + "\n" +
                "카테고리: " + dto.getCategory() + "\n" +
                "태그: " + dto.getMainTag() + "\n" +
                "알레르기 정보: " + dto.getAllergens(),
                meta
        );

        // 문서를 토큰 기준으로 분할 (chunking)
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> docs = splitter.apply(List.of(doc));

        // 분할된 문서들을 벡터 저장소에 추가
        vectorStore.add(docs);
    }

    // 여러 개 상품 등록 + 벡터화
    @Override
    public void addProducts(List<ProductVectorDTO> list) {

        list.forEach(this::addProduct);
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
    public String recommend(String userQuery) {
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
