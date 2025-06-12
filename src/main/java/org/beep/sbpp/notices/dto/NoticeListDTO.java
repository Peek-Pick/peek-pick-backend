package org.beep.sbpp.notices.dto;

import java.time.LocalDateTime;
import org.beep.sbpp.admin.notice.entity.Notice;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 공지사항 목록 조회용 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeListDTO {

    private Long noticeId;
    private String title;
    private LocalDateTime regDate;

    /** Entity → DTO 변환 */
    public static NoticeListDTO fromEntity(Notice notice) {
        return NoticeListDTO.builder()
                .noticeId(notice.getNoticeId())
                .title(notice.getTitle())
                .regDate(notice.getRegDate())
                .build();
    }
}
