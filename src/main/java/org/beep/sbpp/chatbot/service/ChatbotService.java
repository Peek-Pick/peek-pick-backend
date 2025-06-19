package org.beep.sbpp.chatbot.service;

import org.beep.sbpp.products.dto.ProductDetailDTO;

import java.util.List;

public interface ChatbotService {

    void addProduct(ProductDetailDTO dto);
    void addProducts(List<ProductDetailDTO> list);
    String recommend(String userQuery);
    String getSimilarProductDescriptions(String query);

}
