package org.beep.sbpp.chatbot.service;

import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.chatbot.repository.ChatbotRepository;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.beep.sbpp.products.entities.ProductEntity;
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
                .defaultAdvisors( new MessageChatMemoryAdvisor(new InMemoryChatMemory())
                        ,new QuestionAnswerAdvisor(vectorStore)) // 전역 VectorStore
                .build();
    }


    @Override
    public void addProduct(ProductDetailDTO dto) {
        ProductEntity product = ProductEntity.builder()
                .productId(dto.getProductId())
                .name(dto.getName())
                .description(dto.getDescription())
                .build();

        chatbotRepository.save(product);

        Document doc = new Document(dto.getName() + " " + dto.getDescription());
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> docs = splitter.apply(List.of(doc));

        vectorStore.add(docs);
    }

    @Override
    public void addProducts(List<ProductDetailDTO> list) {
        list.forEach(this::addProduct);
    }

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

        Map<String, Object> params = new HashMap<>();
        params.put("x", userQuery);
        params.put("documents", getSimilarProductDescriptions(userQuery));

        PromptTemplate template = new PromptTemplate(prompt);
        return chatClient.prompt(template.create(params)).call().content();
    }

    public String getSimilarProductDescriptions(String query) {
        List<Document> documents = vectorStore.similaritySearch(
                SearchRequest.builder().query(query).topK(5).build()
        );

        return documents.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n"));
    }




}
