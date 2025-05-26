package org.beep.sbpp.admin.notice.controller;

import org.beep.sbpp.admin.notice.storage.ImageStorageService;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/admin/notices/images")
public class NoticeImageController {

    private final ImageStorageService storageService;

    public NoticeImageController(ImageStorageService storageService) {
        this.storageService = storageService;
    }

    /**
     * 파일 업로드 후 접근 가능한 URL 문자열을 반환.
     * RequestPart 이름은 'file' 로 프론트와 맞춰주세요.
     */
    @PostMapping(
            path = "/upload",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public ResponseEntity<String> upload(@RequestPart("file") MultipartFile file) throws Exception {
        String url = storageService.store(file);
        return ResponseEntity.ok(url);
    }
}
