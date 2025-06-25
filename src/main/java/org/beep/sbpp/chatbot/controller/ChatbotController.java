package org.beep.sbpp.chatbot.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.chatbot.service.ChatbotEmbeddingService;
import org.beep.sbpp.chatbot.service.ChatbotService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chatbot")
@Slf4j
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;
    private final ChatbotEmbeddingService chatbotEmbeddingService;

    // 초기 데이터 벡터화
    @PostMapping("/init")
    public ResponseEntity<String> initVectorStore() {

        chatbotEmbeddingService.vectorizeProductInit();
        chatbotEmbeddingService.vectorizeFaqInit();

        return ResponseEntity.ok("✅ 초기 벡터화 완료!");
    }

    // 유저의 질문을 받아서 분류 + 응답 처리
    @PostMapping("/ask")
    public ResponseEntity<String> ask(@RequestBody String userMessage) {
        String reply = chatbotService.handleUserQuery(userMessage);

        log.info("reply: " + reply);
        return ResponseEntity.ok(reply);
    }

}
