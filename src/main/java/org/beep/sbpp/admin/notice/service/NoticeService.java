package org.beep.sbpp.admin.notice.service;

import java.util.List;

import org.beep.sbpp.admin.notice.domain.Notice;
import org.beep.sbpp.admin.notice.domain.NoticeImage;
import org.beep.sbpp.admin.notice.dto.NoticeRequestDto;
import org.beep.sbpp.admin.notice.dto.NoticeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface NoticeService {
    Page<NoticeResponseDto> getNoticeList(Pageable pageable);
    NoticeResponseDto createNotice(NoticeRequestDto dto);
    NoticeResponseDto getNotice(Long id);
    NoticeResponseDto updateNotice(Long id, NoticeRequestDto dto);
    void deleteNotice(Long id);

    // 새로 추가한 메서드: MultipartFile 리스트를 받아 해당 공지에 이미지 업로드
    void uploadImages(Long noticeId, List<MultipartFile> files);
}
