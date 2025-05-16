package org.beep.sbpp.admin.notice.service;

import org.beep.sbpp.admin.notice.dto.NoticeRequestDto;
import org.beep.sbpp.admin.notice.dto.NoticeResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NoticeService {
    NoticeResponseDto createNotice(NoticeRequestDto dto);
    NoticeResponseDto updateNotice(Long id, NoticeRequestDto dto);
    void deleteNotice(Long id);
    NoticeResponseDto getNotice(Long id);
    Page<NoticeResponseDto> getNoticeList(Pageable pageable);
}
