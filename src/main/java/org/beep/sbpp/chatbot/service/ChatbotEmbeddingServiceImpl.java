// src/main/java/org/beep/sbpp/chatbot/service/ChatbotEmbeddingServiceImpl.java
package org.beep.sbpp.chatbot.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.chatbot.dto.FaqVectorDTO;
import org.beep.sbpp.chatbot.entities.ChatbotFaqEntity;
import org.beep.sbpp.chatbot.repository.ChatbotFaqRepository;
import org.beep.sbpp.chatbot.repository.ChatbotRepository;
import org.beep.sbpp.products.entities.*;
import org.beep.sbpp.products.repository.ProductEnRepository;
import org.beep.sbpp.products.repository.ProductJaRepository;
import org.beep.sbpp.products.repository.ProductKoRepository;
import org.beep.sbpp.products.repository.ProductRepository;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ChatbotEmbeddingServiceImpl implements ChatbotEmbeddingService {

    private final ChatbotRepository    chatbotRepository;    // returns List<ProductBaseEntity>
    private final ChatbotFaqRepository chatbotFaqRepository;
    private final ProductKoRepository   koRepository;
    private final ProductEnRepository   enRepository;
    private final ProductJaRepository   jaRepository;

    @Autowired
    VectorStore vectorStore;

    @Override
    public void vectorizeProductInit(String lang) {
        // 1) 모든 BaseEntity 한 번에 로드
        List<ProductBaseEntity> bases = chatbotRepository.findAll();
        if (bases.isEmpty()) return;

        // 2) 해당 lang의 언어별 엔티티 한 번에 로드
        List<Long> ids = bases.stream().map(ProductBaseEntity::getProductId).toList();
        Map<Long, ProductLangEntity> langMap = switch(lang.toLowerCase().split("[-_]")[0]) {
            case "ko" -> koRepository.findAllById(ids).stream()
                    .collect(Collectors.toMap(ProductKoEntity::getProductId, Function.identity()));
            case "en" -> enRepository.findAllById(ids).stream()
                    .collect(Collectors.toMap(ProductEnEntity::getProductId, Function.identity()));
            case "ja" -> jaRepository.findAllById(ids).stream()
                    .collect(Collectors.toMap(ProductJaEntity::getProductId, Function.identity()));
            default   -> throw new IllegalArgumentException("지원하지 않는 언어: " + lang);
        };

        // 3) 메타 + content 결합하여 Document 생성
        List<Document> docs = bases.stream().map(base -> {
                    ProductLangEntity langE = langMap.get(base.getProductId());
                    Map<String,Object> meta = new HashMap<>();
                    meta.put("type", "product");
                    meta.put("productId", String.valueOf(base.getProductId()));
                    meta.put("name",      langE.getName());
                    meta.put("category",  langE.getCategory());
                    meta.put("barcode",   base.getBarcode());
                    meta.put("imgUrl",    base.getImgUrl());
                    String content =
                            "상품명: "   + langE.getName()        + "\n" +
                                    "설명: "     + langE.getDescription() + "\n" +
                                    "카테고리: " + langE.getCategory()    + "\n" +
                                    "알레르기 정보: " + langE.getAllergens();
                    return new Document(content, meta);
                })
                .flatMap(doc -> new TokenTextSplitter().apply(List.of(doc)).stream())
                .toList();

        vectorStore.add(docs);
    }

    @Override
    public void addProduct(ProductBaseEntity base, ProductLangEntity langE) {
        Map<String,Object> meta = new HashMap<>();
        meta.put("type",      "product");
        meta.put("productId", String.valueOf(base.getProductId()));
        meta.put("name",      langE.getName());
        meta.put("category",  langE.getCategory());
        meta.put("barcode",   base.getBarcode());
        meta.put("imgUrl",    base.getImgUrl());

        String content =
                "상품명: "   + langE.getName()        + "\n" +
                        "설명: "     + langE.getDescription() + "\n" +
                        "카테고리: " + langE.getCategory()    + "\n" +
                        "알레르기: " + langE.getAllergens();

        Document doc = new Document(content, meta);
        List<Document> docs = new TokenTextSplitter().apply(List.of(doc));
        vectorStore.add(docs);
    }

    @Override
    public void addProducts(List<ProductBaseEntity> bases, String lang) {
        if (bases.isEmpty()) return;
        // 언어별 매핑을 다시 구해서 각 base+langE 로 addProduct 호출
        List<Long> ids = bases.stream().map(ProductBaseEntity::getProductId).toList();
        Map<Long, ProductLangEntity> langMap = switch(lang.toLowerCase().split("[-_]")[0]) {
            case "ko" -> koRepository.findAllById(ids).stream()
                    .collect(Collectors.toMap(ProductKoEntity::getProductId, Function.identity()));
            case "en" -> enRepository.findAllById(ids).stream()
                    .collect(Collectors.toMap(ProductEnEntity::getProductId, Function.identity()));
            case "ja" -> jaRepository.findAllById(ids).stream()
                    .collect(Collectors.toMap(ProductJaEntity::getProductId, Function.identity()));
            default   -> throw new IllegalArgumentException("지원하지 않는 언어: " + lang);
        };
        bases.forEach(base -> addProduct(base, langMap.get(base.getProductId())));
    }

    @Override
    public void vectorizeFaqInit() {
        List<ChatbotFaqEntity> faqs = chatbotFaqRepository.findAll();
        List<Document> docs = faqs.stream().map(faq -> {
                    Map<String,Object> meta = new HashMap<>();
                    meta.put("type", "faq");
                    String content =
                            "Q: " + faq.getQuestion() + "\n" +
                                    "A: " + faq.getAnswer()   + "\n" +
                                    "카테고리: " + faq.getCategory();
                    return new Document(content, meta);
                })
                .flatMap(doc -> new TokenTextSplitter().apply(List.of(doc)).stream())
                .toList();
        vectorStore.add(docs);
    }

    @Override
    public void addFaq(FaqVectorDTO dto) {
        Map<String,Object> meta = new HashMap<>();
        meta.put("type", "faq");
        String content =
                "Q: " + dto.getQuestion() + "\n" +
                        "A: " + dto.getAnswer()   + "\n" +
                        "카테고리: " + dto.getCategory();
        Document doc = new Document(content, meta);
        List<Document> docs = new TokenTextSplitter().apply(List.of(doc));
        vectorStore.add(docs);
    }

    @Override
    public void addFaqs(List<FaqVectorDTO> list) {
        list.forEach(this::addFaq);
    }
}
