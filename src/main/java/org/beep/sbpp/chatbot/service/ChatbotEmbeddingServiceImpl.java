package org.beep.sbpp.chatbot.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.chatbot.dto.FaqVectorDTO;
import org.beep.sbpp.chatbot.dto.ProductVectorDTO;
import org.beep.sbpp.chatbot.entities.ChatbotFaqEntity;
import org.beep.sbpp.chatbot.repository.ChatbotFaqRepository;
import org.beep.sbpp.chatbot.repository.ChatbotRepository;
import org.beep.sbpp.products.entities.ProductEntity;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ChatbotEmbeddingServiceImpl implements ChatbotEmbeddingService {

    private final ChatbotRepository chatbotRepository;
    private final ChatbotFaqRepository chatbotFaqRepository;

    @Autowired
    VectorStore vectorStore;

    // 초기 DB 상품 전체 벡터화
    @Override
    public void vectorizeProductInit() {
        List<ProductEntity> products = chatbotRepository.findAll();

        // 상품을 벡터화
        List<Document> docs = products.stream()
                .map(p -> {
                    // metadata 구성: 상품ID, 이름, 카테고리, 태그, 바코드, 이미지
                    Map<String, Object> meta = new HashMap<>();
                    meta.put("type", "product");
                    meta.put("productId", String.valueOf(p.getProductId()));
                    meta.put("name", p.getName() != null ? p.getName() : "알수없음");
                    meta.put("category", p.getCategory() != null ? p.getCategory() : "알수없음");
                    meta.put("mainTag", p.getMainTag() != null ? p.getMainTag() : "알수없음");
                    meta.put("barcode", p.getBarcode() != null ? p.getBarcode() : "알수없음");
                    meta.put("imgUrl", p.getImgUrl() != null ? p.getImgUrl() : "알수없음");
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
        meta.put("type", "product"); //
        meta.put("productId", String.valueOf(dto.getProductId()));
        meta.put("name", dto.getName() != null ? dto.getName() : "알수없음");
        meta.put("category", dto.getCategory() != null ? dto.getCategory() : "알수없음");
        meta.put("mainTag", dto.getMainTag() != null ? dto.getMainTag() : "알수없음");
        meta.put("barcode", dto.getBarcode() != null ? dto.getBarcode() : "알수없음");
        meta.put("imgUrl", dto.getImgUrl() != null ? dto.getImgUrl() : "알수없음");
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

    // 초기 DB FAQ 전체 벡터화
    @Override
    public void vectorizeFaqInit() {
        List<ChatbotFaqEntity> faqs = chatbotFaqRepository.findAll();

        // FAQ 벡터화
        List<Document> docs = faqs.stream()
                .map(faq -> {
                    Map<String, Object> meta = new HashMap<>();
                    meta.put("type", "faq");
                    // Document 생성 (content: 상품명, 설명, 카테고리, 태그, 알레르기 정보 + 원재료, 영양성분 고려중)
                    return new Document(
                            "Q: " + faq.getQuestion() + "\n" +
                                    "A: " + faq.getAnswer() + "\n" +
                                    "카테고리: " + faq.getCategory(),
                            meta
                    );
                })

                // 문서를 토큰 기준으로 나누어(Chunking) 여러 개의 Document로 분할
                .flatMap(doc -> new TokenTextSplitter().apply(List.of(doc)).stream())
                .collect(Collectors.toList());

        vectorStore.add(docs);
    }

    // 단일 FAQ 등록 + 벡터화
    @Override
    public void addFaq(FaqVectorDTO dto) {
        // metadata
        Map<String, Object> meta = new HashMap<>();
        meta.put("type", "faq");
        // content
        Document doc = new Document(
                "Q: " + dto.getQuestion() + "\n" +
                        "A: " + dto.getAnswer() + "\n" +
                        "카테고리: " + dto.getCategory(),
                meta
        );

        // 문서를 토큰 기준으로 분할 (chunking)
        TokenTextSplitter splitter = new TokenTextSplitter();
        List<Document> docs = splitter.apply(List.of(doc));

        // 분할된 문서들을 벡터 저장소에 추가
        vectorStore.add(docs);
    }

    // 여러 개 FAQ 등록 + 벡터화
    @Override
    public void addFaqs(List<FaqVectorDTO> list) {

        list.forEach(this::addFaq);
    }


}
