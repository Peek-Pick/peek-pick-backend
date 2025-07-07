//package org.beep.sbpp.chatbot;
//
//import org.beep.sbpp.chatbot.service.ChatbotEmbeddingService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.TestPropertySource;
//
//@SpringBootTest
//@TestPropertySource(properties = {
//        "spring.datasource.url=jdbc:postgresql://172.22.186.219:5432/postgres",
//        "spring.datasource.username=postgres",
//        "spring.datasource.password=1234"
//        // 기타 필요 설정들
//})
//public class ChatbotServiceTests {
//
//    @Autowired
//    private ChatbotEmbeddingService chatbotEmbeddingService;
//
//    @Test
//    public void testInitEmbedding() {
//        chatbotEmbeddingService.vectorizeProductInit();
//        System.out.println("✅ 초기 벡터화 완료");
//    }
//}


package org.beep.sbpp.chatbot;

import org.beep.sbpp.chatbot.service.ChatbotEmbeddingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:postgresql://172.22.186.219:5432/postgres",
        "spring.datasource.username=postgres",
        "spring.datasource.password=1234"
        // 기타 필요 설정들
})
public class ChatbotServiceTests {

    @Autowired
    private ChatbotEmbeddingService chatbotEmbeddingService;

    @Test
    public void testInitEmbedding() {
        // 다국어 초기 벡터화 테스트 (예: 한국어)
        chatbotEmbeddingService.vectorizeProductInit("ko");
        // FAQ 벡터화도 함께 호출
        chatbotEmbeddingService.vectorizeFaqInit();
        System.out.println("✅ 초기 벡터화 완료 (lang=ko)");
    }
}
