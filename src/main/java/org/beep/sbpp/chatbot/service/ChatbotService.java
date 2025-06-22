package org.beep.sbpp.chatbot.service;

import org.beep.sbpp.chatbot.dto.ProductVectorDTO;
import org.beep.sbpp.products.dto.ProductDetailDTO;

import java.util.List;

public interface ChatbotService {

    String handleUserQuery(String userQuery);
    String classifyQuestion(String userQuestion);
    String getSimilarProductDescriptions(String query);
    String productRecommend(String userQuery);

}
