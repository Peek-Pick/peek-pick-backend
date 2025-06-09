package org.beep.sbpp.review;

import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.reviews.service.ReviewLikeService;
import org.junit.jupiter.api.Assertions;
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
        Long reviewId1 = 4L;

        Long userId1 = 3L;

        // tbl_review_like 테이블에 행 추가
        Long likeId1 = service.toggleReviewLike(reviewId1, userId1);
        log.info(likeId1.toString());

        // is_delete 컬럼 true로 변경
        Long likeId2 = service.toggleReviewLike(reviewId1, userId1);
        log.info(likeId2.toString());

        // is_delete 컬럼 false로 변경
        Long likeId3 = service.toggleReviewLike(reviewId1, userId1);
        log.info(likeId3.toString());

        // 같은 reviewId에 대해 likeId 전부 동일
        Assertions.assertEquals(likeId1, likeId2);
        Assertions.assertEquals(likeId1, likeId3);

        Long userId2 = 4L;

        // tbl_review_like 테이블에 행 추가
        Long likeId4 = service.toggleReviewLike(reviewId1, userId2);
        log.info(likeId1.toString());

        // is_delete 컬럼 true로 변경
        Long likeId5 = service.toggleReviewLike(reviewId1, userId2);
        log.info(likeId2.toString());

        // 같은 reviewId에 대해 likeId 전부 동일
        Assertions.assertEquals(likeId4, likeId5);
    }
}