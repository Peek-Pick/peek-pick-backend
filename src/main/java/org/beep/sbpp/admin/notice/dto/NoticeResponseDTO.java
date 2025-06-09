package org.beep.sbpp.admin.notice.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 공지사항 응답용 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeResponseDTO {

    private Long noticeId;
    private String title;
    private String content;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private List<String> imgUrls;
}
