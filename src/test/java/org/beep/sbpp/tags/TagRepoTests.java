package org.beep.sbpp.tags;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.tags.entities.TagEntity;
import org.beep.sbpp.tags.enums.TagCategoryName;
import org.beep.sbpp.tags.enums.TagName;
import org.beep.sbpp.tags.repository.TagRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Transactional
@Slf4j
@TestPropertySource(properties = {
        "GOOGLE_CLIENT_ID=test-client-id",
        "GOOGLE_CLIENT_SECRET=test-secret",
        "GOOGLE_REDIRECT_URI=http://localhost:8080/oauth2/callback",
        "JWT_SECRET=1232131334554434343424242Ts"
})
public class TagRepoTests {

    @Autowired
    private TagRepository tagRepository;

//    @Test
//    @Commit
//    public void testInsertTags() {
//        for(TagName tagName : TagName.values()) {
//            TagEntity tag = TagEntity.builder()
//                    .tagName(tagName)
//                    .build();
//            tagRepository.save(tag);
//        }
//    }

    @Test
    @Commit
    public void testInsertTags() {
        for (TagName tagName : TagName.values()) {
            TagCategoryName category = determineCategory(tagName); // 이름에 따라 카테고리 매핑

            TagEntity tag = TagEntity.builder()
                    .tagName(tagName)
                    .category(category) // ✅ 추가
                    .build();

            tagRepository.save(tag);
        }
    }

    private TagCategoryName determineCategory(TagName tagName) {
        return switch (tagName) {
            case SWEET, SAVORY, SPICY, NUTTY, MILD, BITTER, SOUR -> TagCategoryName.TASTE;
            case CRISPY, CHEWY, MOIST, SOFT, TOUGH, DRY -> TagCategoryName.TEXTURE;
            case BUTTERY, CHEESY, GARLICKY, SMOKY -> TagCategoryName.FLAVOR;
            case RICH, CLEAN, FILLING, ADDICTIVE, GREASY -> TagCategoryName.OTHER;
            case LOW_CALORIE, HIGH_PROTEIN, LOW_SUGAR, VEGETARIAN, VEGAN, GLUTEN_FREE -> TagCategoryName.HEALTH;
        };
    }

}
