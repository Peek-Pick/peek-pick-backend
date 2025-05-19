package org.beep.sbpp.review;

import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.reviews.service.ReviewLikeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class ReviewLikeServiceTest {
    @Autowired
    private ReviewLikeService service;

    @Test
    public void testToggleReviewLike() {
        // 테스트 코드 구현 필요
    }
}