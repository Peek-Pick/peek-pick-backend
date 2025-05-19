package org.beep.sbpp.admin.notice.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.beep.sbpp.admin.notice.dto.NoticeRequestDTO;
import org.beep.sbpp.admin.notice.dto.NoticeResponseDTO;
import org.beep.sbpp.admin.notice.service.NoticeService;

@RestController
@RequestMapping("/admin/notices")
@Validated
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping
    public ResponseEntity<Page<NoticeResponseDTO>> list(
            @PageableDefault(page = 0, size = 10, sort = "regDate", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable) {
        Page<NoticeResponseDTO> page = noticeService.getNoticeList(pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping
    public ResponseEntity<NoticeResponseDTO> create(
            @RequestBody @Valid NoticeRequestDTO dto) {
        return ResponseEntity.ok(noticeService.createNotice(dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<NoticeResponseDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(noticeService.getNotice(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoticeResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid NoticeRequestDTO dto) {
        return ResponseEntity.ok(noticeService.updateNotice(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }

    // 새로 추가한 이미지 업로드 엔드포인트
    @PostMapping("/{id}/images")
    public ResponseEntity<Void> uploadImages(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files) {
        noticeService.uploadImages(id, files);
        return ResponseEntity.ok().build();
    }
}
