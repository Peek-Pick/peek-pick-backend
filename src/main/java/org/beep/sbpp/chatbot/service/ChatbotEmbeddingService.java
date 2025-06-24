package org.beep.sbpp.chatbot.service;

import org.beep.sbpp.chatbot.dto.ProductVectorDTO;

import java.util.List;

public interface ChatbotEmbeddingService {

    void vectorizeInit();
    void addProduct(ProductVectorDTO dto);
    void addProducts(List<ProductVectorDTO> list);
}
