package org.beep.sbpp.reviews.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.reviews.service.ReviewTranslateService;
import org.beep.sbpp.util.UserInfoUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/reviews/translate")
@RequiredArgsConstructor
public class ReviewTranslateController {
    private final ReviewTranslateService reviewTranslateService;
    private final UserInfoUtil userInfoUtil;

    // 리뷰 요약 호출
    @GetMapping("/{reviewId}")
    public ResponseEntity<String> countReviewsByUserId(@PathVariable Long reviewId,
                                                     HttpServletRequest request) {
        Long userId = userInfoUtil.getAuthUserId(request);

        String translated = reviewTranslateService.translate(userId, reviewId);

        return ResponseEntity.ok(translated);
    }
}