package org.beep.sbpp.admin.notice.dto;

import java.util.List;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 공지사항 생성/수정 요청 시 사용되는 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminNoticeRequestDTO {

    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @NotBlank(message = "내용은 필수입니다.")
    private String content;

    /** 클라이언트가 업로드한 후 받은 이미지 URL 리스트 */
    private List<String> imgUrls;
}
