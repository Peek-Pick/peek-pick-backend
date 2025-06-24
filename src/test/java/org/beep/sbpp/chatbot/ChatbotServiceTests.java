package org.beep.sbpp.chatbot;

import org.beep.sbpp.chatbot.service.ChatbotEmbeddingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest

public class ChatbotServiceTests {

    @Autowired
    private ChatbotEmbeddingService chatbotEmbeddingService;

    @Test
    public void testInitEmbedding() {
        chatbotEmbeddingService.vectorizeInit();
        System.out.println("✅ 초기 벡터화 완료");
    }
}
