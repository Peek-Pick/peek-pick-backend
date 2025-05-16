package org.beep.sbpp.review;

import lombok.extern.log4j.Log4j2;
import org.beep.sbpp.reviews.repository.ReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Log4j2
@SpringBootTest
public class ReviewRepositoryTest {
    @Autowired
    private ReviewRepository repository;

    @Test
    public void testSelectOne() {
    }

    @Test
    public void testUpdateOne() {

    }

    @Test
    public void testDeleteOne() {

    }
}