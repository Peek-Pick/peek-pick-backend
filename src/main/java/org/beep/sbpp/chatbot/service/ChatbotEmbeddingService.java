// src/main/java/org/beep/sbpp/chatbot/service/ChatbotEmbeddingService.java
package org.beep.sbpp.chatbot.service;

import org.beep.sbpp.chatbot.dto.FaqVectorDTO;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.products.entities.ProductLangEntity;

import java.util.List;

public interface ChatbotEmbeddingService {

    // ◆ 상품 벡터화 ◆

    /**
     * 초기 DB 상품 전체 벡터화 (주어진 언어로)
     * @param lang "ko", "en", "ja" 등
     */
    void vectorizeProductInit(String lang);

    /**
     * 단일 상품 추가 벡터화
     * @param base   ProductBaseEntity
     * @param langE  언어별 엔티티 (ProductLangEntity)
     */
    void addProduct(ProductBaseEntity base, ProductLangEntity langE);

    /**
     * 여러 상품 추가 벡터화
     * @param bases  ProductBaseEntity 목록
     * @param lang   언어 키
     */
    void addProducts(List<ProductBaseEntity> bases, String lang);


    // ◆ FAQ 벡터화 ◆

    void vectorizeFaqInit();
    void addFaq(FaqVectorDTO dto);
    void addFaqs(List<FaqVectorDTO> list);
}
