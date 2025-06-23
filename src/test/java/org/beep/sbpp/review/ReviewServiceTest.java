package org.beep.sbpp.review;

import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.reviews.dto.ReviewDetailDTO;
import org.beep.sbpp.reviews.dto.ReviewImgDTO;
import org.beep.sbpp.reviews.dto.ReviewModifyDTO;
import org.beep.sbpp.reviews.dto.ReviewSimpleDTO;
import org.beep.sbpp.reviews.service.ReviewService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.List;

@Slf4j
@SpringBootTest
public class ReviewServiceTest {
    @Autowired
    private ReviewService service;

//    @Test
//    public void testGetOne() {
//        Long reviewId = 100L;
//        ReviewSimpleDTO review = service.getOne(reviewId);
//        log.info(review.toString());
//    }
//
//    @Test
//    public void testGetOneDetail() {
//        Long reviewId = 100L;
//        ReviewDetailDTO review = service.getOneDetail(reviewId);
//        log.info(review.toString());
//    }
//
//    @Test
//    public void testRegister() {
//        for (int i = 1; i <= 123; i++) {
//            ReviewDetailDTO dto = ReviewDetailDTO.builder()
//                    .userId(1L)
//                    .comment("register 테스트" + i)
//                    .score(i % 6)
//                    .images(imageList)
//                    .build();
//
//            Long ReviewId = service.register(dto);
//            log.info(ReviewId.toString());
//        }
//    }

//    @Test
//    public void testModify() {
//        Long reviewId = 101L;
//
//        ReviewModifyDTO dto = ReviewModifyDTO.builder()
//                .reviewId(reviewId)
//                .comment("modify 테스트" + reviewId)
//                .score(5)
//                .deleteImgIds(List.of(301L, 302L, 303L))
//                .newImgUrls(List.of("test_img4.jpg","test_img5.jpg"))
//                .build();
//
//        Long result = service.modify(reviewId, dto);
//        log.info(result.toString());
//
//        ReviewDetailDTO modReview = service.getOneDetail(reviewId);
//        log.info(modReview.toString());
//    }
//
//    @Test
//    public void testDelete() {
//        Long reviewId = 100L;
//
//        Long result = service.delete(reviewId);
//        log.info(result.toString());
//    }
}