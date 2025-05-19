package org.beep.sbpp.review;

import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.reviews.dto.ReviewDTO;
import org.beep.sbpp.reviews.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class ReviewServiceTest {
    @Autowired
    private ReviewService service;

    @Test
    public void testGetOne() {
        Long reviewId = 150L;
        ReviewDTO review = service.getOne(reviewId);
        log.info(review.toString());
    }

    @Test
    public void testRegister() {
        for (int i = 1; i <= 123; i++) {
            ReviewDTO dto = ReviewDTO.builder()
                    .comment("register 테스트" + i)
                    .score(i % 6)
                    .build();

            Long ReviewId = service.register(dto);
            log.info(ReviewId.toString());
        }
    }

    @Test
    public void testModify() {
        Long reviewId = 150L;

        ReviewDTO review = service.getOne(reviewId);

        String modComment = "modify 테스트" + reviewId;
        Integer modScore = review.getScore();

        Long result = service.modify(reviewId, modComment, modScore);
        log.info(result.toString());

        ReviewDTO modReview = service.getOne(reviewId);
        log.info(modReview.toString());
    }

    @Test
    public void testDelete() {
        Long reviewId = 151L;

        Long result = service.delete(reviewId);
        log.info(result.toString());
    }
}