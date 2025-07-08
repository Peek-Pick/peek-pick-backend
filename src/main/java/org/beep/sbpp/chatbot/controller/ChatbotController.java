package org.beep.sbpp.chatbot.controller;


import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.chatbot.service.ChatbotEmbeddingService;
import org.beep.sbpp.chatbot.service.ChatbotService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<String> initVectorStore(@RequestParam(name = "lang", defaultValue = "ko")String lang) {

        try {
            chatbotEmbeddingService.vectorizeProductInit(lang);
            chatbotEmbeddingService.vectorizeFaqInit();
            return ResponseEntity.ok("✅ 초기 벡터화 완료!");
        } catch (Exception ex) {
            log.error("초기 벡터화 실패", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error:초기 벡터화 실패");
        }
    }

    // 유저의 질문을 받아서 분류 + 응답 처리
    @PostMapping("/ask")
    public ResponseEntity<String> ask(@RequestBody String userMessage, HttpSession session) {
        try {
            String reply = chatbotService.handleUserQuery(userMessage, session);
            log.info("reply: " + reply);
            return ResponseEntity.ok(reply);
        } catch (Exception ex) {
            log.error("응답 처리 실패", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error: 응답처리 실패");
        }
    }

    // Chat 메모리 초기화
    @PostMapping("/reset")
    public ResponseEntity<Void> resetMemory(HttpSession session) {
        try {
            session.removeAttribute("chat_memory");
            return ResponseEntity.ok().build();
        } catch (Exception ex) {
            log.error("메모리 초기화 실패", ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
