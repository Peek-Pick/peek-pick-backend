package org.beep.sbpp.tags;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.tags.entities.TagEntity;
import org.beep.sbpp.tags.enums.TagName;
import org.beep.sbpp.tags.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;

@SpringBootTest
@Transactional
@Slf4j
public class TagRepoTests {

    @Autowired
    private TagRepository tagRepository;

    @Test
    @Commit
    public void testInsertTags() {
        for(TagName tagName : TagName.values()) {
            TagEntity tag = TagEntity.builder()
                    .tagName(tagName)
                    .build();
            tagRepository.save(tag);
        }
    }

}
