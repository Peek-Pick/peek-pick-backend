package org.beep.sbpp.notices.dto;

import java.time.LocalDateTime;
import java.util.List;
import org.beep.sbpp.admin.notice.entity.Notice;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 공지사항 상세 조회용 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeDetailDTO {

    private Long noticeId;
    private String title;
    private String content;
    private LocalDateTime regDate;
    private LocalDateTime modDate;
    private List<String> imgUrls;

    /** Entity → DTO 변환 */
    public static NoticeDetailDTO fromEntity(Notice notice) {
        return NoticeDetailDTO.builder()
                .noticeId(notice.getNoticeId())
                .title(notice.getTitle())
                .content(notice.getContent())
                .regDate(notice.getRegDate())
                .modDate(notice.getModDate())
                .imgUrls(
                        notice.getImages()
                                .stream()
                                .map(img -> img.getImgUrl())
                                .toList()
                )
                .build();
    }
}
