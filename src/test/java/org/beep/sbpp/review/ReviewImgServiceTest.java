package org.beep.sbpp.review;

import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.reviews.dto.ReviewImgDTO;
import org.beep.sbpp.reviews.service.ReviewImgService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@SpringBootTest
public class ReviewImgServiceTest {
    @Autowired
    private ReviewImgService service;

    @Test
    public void testRegisterImg() {
        Long reviewId = 150L;

        for (int i = 1; i < 5; i++) {
            ReviewImgDTO imgDto = ReviewImgDTO.builder()
                    .reviewId(reviewId)
                    .imgUrl("test_img" + i)
                    .build();

            Long imgId = service.registerImg(imgDto);
            log.info(imgId.toString());
        }
    }

    @Test
    public void testGetImgOne() {
        Long reviewId = 150L;

        ReviewImgDTO reviewImgDTO = service.getImgOne(reviewId);
        log.info(reviewImgDTO.toString());
    }

    @Test
    public void testGetImgAll() {
        Long reviewId = 150L;

        List<ReviewImgDTO> reviewImgDTOS = service.getImgAll(reviewId);
        log.info(reviewImgDTOS.toString());
    }

    @Test
    public void testDeleteImg() {
        Long reviewImgId = 1L;

        Long imgId = service.deleteImg(reviewImgId);
        log.info(imgId.toString());
    }

    @Test
    public void testModifyImg() {
        Long reviewId = 150L;

        List<Long> deleteImgIds = new ArrayList<>();
        deleteImgIds.add(2L);

        List<String> newImgUrls = new ArrayList<>();
        newImgUrls.add("test_img5");
        newImgUrls.add("test_img6");

        Long result = service.modifyImg(reviewId, deleteImgIds, newImgUrls);
        log.info(result.toString());
    }
}