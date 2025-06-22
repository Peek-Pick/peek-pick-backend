package org.beep.sbpp.chatbot.service;

import org.beep.sbpp.chatbot.dto.ProductVectorDTO;
import org.beep.sbpp.products.dto.ProductDetailDTO;

import java.util.List;

public interface ChatbotService {


    void vectorizeInit();
    void addProduct(ProductVectorDTO dto);
    void addProducts(List<ProductVectorDTO> list);
    String getSimilarProductDescriptions(String query);
    String recommend(String userQuery);

}
