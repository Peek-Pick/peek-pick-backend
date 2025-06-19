package org.beep.sbpp.admin.notice.controller;

import org.beep.sbpp.admin.notice.service.AdminNoticeImageStorageService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 공지사항 이미지 전용 업로드 엔드포인트
 */
@RestController
@RequestMapping("/api/v1/admin/notices/images")
public class AdminNoticeImageController {

    private final AdminNoticeImageStorageService imageStorageService;

    public AdminNoticeImageController(AdminNoticeImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }

    /**
     * 파일 업로드 후, Nginx 아래에 저장된 경로를 반환
     * RequestPart 이름은 'file'로 프론트와 맞춤
     *
     * @param file MultipartFile (HTML formData에서 key="file")
     * @return 저장된 이미지 URL 문자열 (예: "/upload/notices/{uuid}.png")
     */
    @PostMapping(
            path = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> upload(@RequestPart("file") MultipartFile file) throws Exception {
        String url = imageStorageService.store(file);
        return ResponseEntity.ok(url);
    }
}
