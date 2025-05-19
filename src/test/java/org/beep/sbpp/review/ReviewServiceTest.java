package org.beep.sbpp.review;

import lombok.extern.log4j.Log4j2;
import org.beep.sbpp.reviews.dto.ReviewDTO;
import org.beep.sbpp.reviews.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Log4j2
@SpringBootTest
public class ReviewServiceTest {
    @Autowired
    private ReviewService service;

    @Test
    public void testGetOne() {
        Long reviewId = 150L;
        ReviewDTO review = service.getOne(reviewId);
        log.info(review);
    }

    @Test
    public void testRegister() {
        for (int i = 1; i <= 123; i++) {
            ReviewDTO dto = ReviewDTO.builder()
                    .comment("register 테스트" + i)
                    .score(i % 6)
                    .build();

            Long ReviewId = service.register(dto);
        }
    }

    @Test
    public void testModify() {
        Long reviewId = 100L;

        ReviewDTO review = service.getOne(reviewId);

        String modComment = "modify 테스트" + reviewId;
        Integer modScore = review.getScore();

        Long result = service.modify(reviewId, modComment, modScore);
        log.info(result);

        ReviewDTO modReview = service.getOne(reviewId);
        log.info(modReview);
    }

    @Test
    public void testDelete() {
        Long reviewId = 151L;

        Long result = service.delete(reviewId);
        log.info(result);
    }
}