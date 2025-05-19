package org.beep.sbpp.admin.notice.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.beep.sbpp.admin.notice.dto.NoticeRequestDto;
import org.beep.sbpp.admin.notice.dto.NoticeResponseDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")  // src/test/resources/application-test.properties 사용
@Transactional
@Rollback(false)
public class NoticeServiceIntegrationTest {

    @Autowired
    private NoticeService noticeService;

    private Long savedId;

    @BeforeEach
    void setUp() {
        // 테스트용 공지 하나 미리 생성
        NoticeRequestDto dto = NoticeRequestDto.builder()
                .title("테스트 공지")
                .content("테스트 내용")
                .imgUrls(List.of("http://img1.jpg", "http://img2.jpg"))
                .build();

        NoticeResponseDto created = noticeService.createNotice(dto);
        savedId = created.getNoticeId();
    }

    @AfterEach
    void tearDown() {
        // 트랜잭션 롤백 처리로 별도 삭제 불필요
    }

    @Test
    void createAndGetNotice() {
        NoticeResponseDto notice = noticeService.getNotice(savedId);
        assertThat(notice.getTitle()).isEqualTo("테스트 공지");
        assertThat(notice.getImgUrls()).containsExactly("http://img1.jpg", "http://img2.jpg");
    }

    @Test
    void updateNotice_addAndRemoveImages() {
        // 기존 이미지 하나만 남기고, 새 이미지 하나 추가
        NoticeRequestDto updateDto = NoticeRequestDto.builder()
                .title("수정된 공지")
                .content("수정된 내용")
                .imgUrls(List.of("http://img2.jpg", "http://img3.jpg"))
                .build();

        NoticeResponseDto updated = noticeService.updateNotice(savedId, updateDto);

        assertThat(updated.getTitle()).isEqualTo("수정된 공지");
        assertThat(updated.getContent()).isEqualTo("수정된 내용");
        assertThat(updated.getImgUrls())
                .containsExactlyInAnyOrder("http://img2.jpg", "http://img3.jpg");
    }

    @Test
    void listNotices_paging() {
        // 한 건만 생성했으니, page size 5로 조회 시 totalElements = 1
        var page = noticeService.getNoticeList(org.springframework.data.domain.PageRequest.of(0, 5));
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    void deleteNotice() {
        noticeService.deleteNotice(savedId);
        // 삭제 후 조회 시 예외 발생
        try {
            noticeService.getNotice(savedId);
        } catch (RuntimeException ex) {
            assertThat(ex.getMessage()).contains("존재하지 않는 공지");
        }
    }
}
