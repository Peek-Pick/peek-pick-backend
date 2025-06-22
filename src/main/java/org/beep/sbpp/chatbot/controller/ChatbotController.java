package org.beep.sbpp.chatbot.controller;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.chatbot.dto.ProductVectorDTO;
import org.beep.sbpp.chatbot.service.ChatbotService;
import org.beep.sbpp.products.dto.ProductDetailDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/v1/chatbot")
@Slf4j
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    // 상품 임베딩 추가
    // 1. 여러 개의 상품 정보를 한 번에 벡터스토어(DB)에 저장 (임베딩 포함) - 초기 상품 데이터 셋팅 시 사용
    @PostMapping("/addAll")
    public ResponseEntity<List<ProductVectorDTO>> productsAdd(@RequestBody List<ProductVectorDTO> list) {

        chatbotService.addProducts(list);
        return ResponseEntity.ok(list);
    }

    // 2. 단일 상품 임베딩 추가
    @PostMapping("/add")
    public ResponseEntity<ProductVectorDTO> productAdd(@RequestBody ProductVectorDTO dto) {

        chatbotService.addProduct(dto);
        return ResponseEntity.created(null).body(dto);
    }

    // 초기 상품 데이터 벡터화
    @PostMapping("/init")
    public ResponseEntity<String> initVectorStore() {

        chatbotService.vectorizeInit();
        return ResponseEntity.ok("✅ 초기 벡터화 완료!");
    }

    // 유사 상품 검색 - 사용자의 문장을 임베딩 후 pgvector에서 유사한 상품 설명을 검색 -> 가장 유사한 상품들을 기반으로 답변 생성
    @GetMapping("/searchString")
    public String similar(@RequestParam String query) throws IOException {
        String response = chatbotService.getSimilarProductDescriptions(query);

        return response;
    }
}
