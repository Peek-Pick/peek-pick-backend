package org.beep.sbpp.admin.notice.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 이미지 저장 기능을 추상화한 인터페이스
 */
public interface AdminNoticeImageStorageService {
    /**
     * MultipartFile을 받아 Nginx 루트 디렉터리 아래에 저장하고,
     * 클라이언트가 접근 가능한 URL 경로 문자열을 반환한다.
     *
     * @param file 업로드할 MultipartFile
     * @return 저장된 이미지에 대한 URL (예: "/upload/notices/UUID.png")
     */
    String store(MultipartFile file);
}
