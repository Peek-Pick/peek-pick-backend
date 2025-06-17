package org.beep.sbpp.admin.notice.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.beep.sbpp.admin.notice.dto.AdminNoticeRequestDTO;
import org.beep.sbpp.admin.notice.dto.AdminNoticeResponseDTO;
import org.beep.sbpp.admin.notice.service.AdminNoticeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 공지사항 CRUD용 메인 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/admin/notices")
@Validated
public class AdminNoticeController {

    private final AdminNoticeService noticeService;

    public AdminNoticeController(AdminNoticeService noticeService) {
        this.noticeService = noticeService;
    }

    /** 페이징된 공지 목록 조회 */
    @GetMapping
    public ResponseEntity<Page<AdminNoticeResponseDTO>> list(
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "regDate",
                    direction = org.springframework.data.domain.Sort.Direction.DESC
            ) Pageable pageable) {
        Page<AdminNoticeResponseDTO> page = noticeService.getNoticeList(pageable);
        return ResponseEntity.ok(page);
    }

    /** 단일 공지 생성 */
    @PostMapping
    public ResponseEntity<AdminNoticeResponseDTO> create(
            @RequestBody @Valid AdminNoticeRequestDTO dto) {
        return ResponseEntity.ok(noticeService.createNotice(dto));
    }

    /** 단일 공지 조회 */
    @GetMapping("/{id}")
    public ResponseEntity<AdminNoticeResponseDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getNotice(id));
    }

    /** 단일 공지 수정 */
    @PutMapping("/{id}")
    public ResponseEntity<AdminNoticeResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid AdminNoticeRequestDTO dto) {
        return ResponseEntity.ok(noticeService.updateNotice(id, dto));
    }

    /** 단일 공지 삭제 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }

    /** 이미지 업로드 */
    @PostMapping("/{id}/images")
    public ResponseEntity<Void> uploadImages(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files) {
        noticeService.uploadImages(id, files);
        return ResponseEntity.ok().build();
    }
}
