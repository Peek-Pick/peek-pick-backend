package org.beep.sbpp.chatbot.service;

import org.beep.sbpp.chatbot.dto.FaqVectorDTO;
import org.beep.sbpp.products.entities.ProductEntity;

import java.util.List;

public interface ChatbotEmbeddingService {

    // product
    void vectorizeProductInit();
    void addProduct(ProductEntity p);
    void addProducts(List<ProductEntity> list);

    // FAQ
    void vectorizeFaqInit();
    void addFaq(FaqVectorDTO dto);
    void addFaqs(List<FaqVectorDTO> list);

}
