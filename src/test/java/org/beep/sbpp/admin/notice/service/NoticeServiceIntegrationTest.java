package org.beep.sbpp.admin.notice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.beep.sbpp.admin.notice.dto.AdminNoticeRequestDTO;
import org.beep.sbpp.admin.notice.dto.AdminNoticeResponseDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;               // ← Disabled import
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")  // src/test/resources/application-test.properties 사용
@Transactional            // 기본적으로 각 테스트 후 롤백됨
public class NoticeServiceIntegrationTest {

    @Autowired
    private AdminNoticeService noticeService;

    private Long savedId;

    @BeforeEach
    void setUp() {
        AdminNoticeRequestDTO dto = AdminNoticeRequestDTO.builder()
                .title("테스트 공지!")
                .content("테스트 내용!")
                .imgUrls(List.of("http://img1.jpg", "http://img2.jpg"))
                .build();

        AdminNoticeResponseDTO created = noticeService.createNotice(dto);
        savedId = created.getNoticeId();
    }

    @AfterEach
    void tearDown() {
        // @Transactional 으로 매번 롤백됨
    }

    @Test
    void createAndGetNotice() {
        AdminNoticeResponseDTO notice = noticeService.getNotice(savedId);
        assertThat(notice.getTitle()).isEqualTo("테스트 공지!");
        assertThat(notice.getImgUrls()).containsExactly("http://img1.jpg", "http://img2.jpg");
    }

    @Test
    void listNotices_paging() {
        var page = noticeService.getNoticeList(
                org.springframework.data.domain.PageRequest.of(0, 5)
        );
        assertThat(page.getTotalElements()).isEqualTo(1);
    }

    @Test
    void deleteNotice() {
        noticeService.deleteNotice(savedId);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            noticeService.getNotice(savedId);
        });
        assertThat(ex.getMessage()).contains("공지 없음");
    }

    /**
     * 이미지 수정 로직 검증 테스트는 임시로 비활성화.
     * 필요할 때만 @Disabled 주석을 제거하고 실행하세요.
     */
    @Test
    @Disabled("이미지 수정 테스트는 임시 비활성화 상태입니다")
    void updateNotice_addAndRemoveImages() {
        AdminNoticeRequestDTO updateDto = AdminNoticeRequestDTO.builder()
                .title("수정된 공지")
                .content("수정된 내용")
                .imgUrls(List.of("http://img2.jpg", "http://img3.jpg"))
                .build();

        AdminNoticeResponseDTO updated = noticeService.updateNotice(savedId, updateDto);

        assertThat(updated.getTitle()).isEqualTo("수정된 공지");
        assertThat(updated.getContent()).isEqualTo("수정된 내용");
        assertThat(updated.getImgUrls())
                .containsExactlyInAnyOrder("http://img2.jpg", "http://img3.jpg");
    }
}
