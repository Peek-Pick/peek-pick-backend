package org.beep.sbpp.notices.service;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.beep.sbpp.notices.dto.NoticeListDTO;
import org.beep.sbpp.notices.dto.NoticeDetailDTO;

/**
 * 공지사항 조회용 비즈니스 로직 인터페이스
 */
public interface NoticeService {

    /** 페이징된 공지 목록 */
    Page<NoticeListDTO> getNoticeList(Pageable pageable);

    /** 단일 공지 상세 */
    NoticeDetailDTO getNotice(Long id);
}
