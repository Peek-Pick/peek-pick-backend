package org.beep.sbpp.notices.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;

import org.beep.sbpp.notices.repository.NoticeRepository;
import org.beep.sbpp.notices.dto.NoticeListDTO;
import org.beep.sbpp.notices.dto.NoticeDetailDTO;
import org.beep.sbpp.admin.notice.controller.AdminNoticeNotFoundException;
import org.beep.sbpp.admin.notice.entity.Notice;

/**
 * 공지사항 조회용 Service 구현체
 */
@Service
@Transactional(readOnly = true)
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;

    public NoticeServiceImpl(NoticeRepository noticeRepository) {
        this.noticeRepository = noticeRepository;
    }

    @Override
    public Page<NoticeListDTO> getNoticeList(Pageable pageable) {
        return noticeRepository.findAll(pageable)
                .map(NoticeListDTO::fromEntity);
    }

    @Override
    public NoticeDetailDTO getNotice(Long id) {
        Notice notice = noticeRepository.findById(id)
                .orElseThrow(() -> new AdminNoticeNotFoundException(id));
        return NoticeDetailDTO.fromEntity(notice);
    }
}
