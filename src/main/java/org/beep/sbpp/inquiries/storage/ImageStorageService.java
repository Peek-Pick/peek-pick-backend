package org.beep.sbpp.inquiries.storage;

import org.springframework.web.multipart.MultipartFile;

public interface ImageStorageService {
    String store(MultipartFile file) throws Exception;
}
/**
 * 파일을 저장하고 접근 가능한 URL을 반환한다.
 * @param file 업로드된 MultipartFile
 * @return 이미지 URL (서버 리소스 핸들러 또는 외부 스토리지 URL)
 */