package org.beep.sbpp.chatbot.service;

import org.beep.sbpp.chatbot.dto.FaqVectorDTO;
import org.beep.sbpp.chatbot.dto.ProductVectorDTO;

import java.util.List;

public interface ChatbotEmbeddingService {

    // product
    void vectorizeProductInit();
    void addProduct(ProductVectorDTO dto);
    void addProducts(List<ProductVectorDTO> list);

    // FAQ
    void vectorizeFaqInit();
    void addFaq(FaqVectorDTO dto);
    void addFaqs(List<FaqVectorDTO> list);

}
