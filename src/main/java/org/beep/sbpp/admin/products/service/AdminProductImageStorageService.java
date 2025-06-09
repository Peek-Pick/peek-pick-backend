package org.beep.sbpp.admin.products.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 상품 이미지 저장용 인터페이스
 */
public interface AdminProductImageStorageService {
    /**
     * MultipartFile을 저장하고,
     * 클라이언트가 접근 가능한 절대 URL을 반환한다.
     * 예: "http://localhost/upload/products/{uuid.ext}"
     */
    String store(MultipartFile file);
}
