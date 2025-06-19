package org.beep.sbpp.reviews.controller;

import lombok.RequiredArgsConstructor;
import org.beep.sbpp.reviews.service.ReviewSentimentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/reviews/")
@RequiredArgsConstructor
public class ReviewSummaryController {

    private final ReviewSentimentService service;

    @GetMapping("/sentiment")
    public ResponseEntity<Float> analyze(@RequestParam String text) throws Exception {
        float score = service.testAnalyzeSentiment(text);
        return ResponseEntity.ok(score);
    }
}
