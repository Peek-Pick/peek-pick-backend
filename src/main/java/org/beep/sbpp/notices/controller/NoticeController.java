package org.beep.sbpp.notices.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;

import org.beep.sbpp.notices.service.NoticeService;
import org.beep.sbpp.notices.dto.NoticeListDTO;
import org.beep.sbpp.notices.dto.NoticeDetailDTO;

/**
 * 공지사항 조회(일반 사용자용) 엔드포인트
 */
@RestController
@RequestMapping("/api/v1/notices")
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    /** 페이징된 공지 목록 조회 */
    @GetMapping
    public ResponseEntity<Page<NoticeListDTO>> list(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "regDate",
                    direction = Sort.Direction.DESC
            ) Pageable pageable
    ) {
        Page<NoticeListDTO> page = noticeService.getNoticeList(pageable);
        return ResponseEntity.ok(page);
    }

    /** 단일 공지 상세 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<NoticeDetailDTO> get(@PathVariable Long id) {
        NoticeDetailDTO dto = noticeService.getNotice(id);
        return ResponseEntity.ok(dto);
    }
}
