package org.beep.sbpp.tags;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.tags.dto.TagDTO;
import org.beep.sbpp.tags.service.TagService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Transactional
@Slf4j
public class TagServiceTests {

    @Autowired
    private TagService tagService;

    @Test
    void testGetAllTagNames() {
        List<TagDTO> tags = tagService.getAllTagNames();
        log.info("Tags: {}", tags);
    }
}
