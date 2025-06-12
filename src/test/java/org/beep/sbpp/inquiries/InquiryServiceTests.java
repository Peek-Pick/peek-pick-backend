package org.beep.sbpp.inquiries;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.inquiries.entities.Inquiry;
import org.beep.sbpp.inquiries.entities.InquiryImage;
import org.beep.sbpp.inquiries.repository.InquiryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;

import java.util.stream.Collectors;

@SpringBootTest
@Slf4j
@TestPropertySource(properties = {
        "GOOGLE_CLIENT_ID=test-client-id",
        "GOOGLE_CLIENT_SECRET=test-secret",
        "GOOGLE_REDIRECT_URI=http://localhost:8080/oauth2/callback",
        "JWT_SECRET=1232131334554434343424242Ts"
})
public class InquiryServiceTests {

    @Autowired
    private InquiryRepository inquiryRepository;

    @Test
    @Transactional
    @Rollback(false)  // ⬅️ 테스트가 끝나도 DB에 반영
    void duplicateInquiries() {
        var originalList = inquiryRepository.findAll();

        log.info("원래 문의 개수: {}", originalList.size());

        for (Inquiry original : originalList) {
            Inquiry copied = Inquiry.builder()
                    .userEntity(original.getUserEntity())
                    .title(original.getTitle() + " (복사본)")
                    .content(original.getContent())
                    .type(original.getType())
                    .status(original.getStatus())
                    .isDelete(false)
                    .build();

            // 이미지 복사
            var copiedImages = original.getImages().stream().map(img -> {
                InquiryImage copiedImg = InquiryImage.builder()
                        .imgUrl(img.getImgUrl())
                        .build();
                copiedImg.setInquiry(copied);  // ⬅️ 역참조 설정
                return copiedImg;
            }).collect(Collectors.toList());

            copied.getImages().addAll(copiedImages);

            inquiryRepository.save(copied);
        }

        log.info("복제 완료. 총 문의 개수: {}", inquiryRepository.count());
    }
}
