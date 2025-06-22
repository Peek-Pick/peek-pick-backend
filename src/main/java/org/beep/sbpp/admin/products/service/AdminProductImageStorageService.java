package org.beep.sbpp.admin.products.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 상품 이미지 저장용 인터페이스
 */
public interface AdminProductImageStorageService {
    /**
     * MultipartFile을 저장하고,
     * 클라이언트가 접근 가능한 URL 2개를 반환한다.
     * [0] = 원본 이미지 URL (/products/pp-{barcode}.확장자)
     * [1] = 썸네일 이미지 URL (/product_thumbnail/pp-{barcode}-thumb.확장자) 또는 null
     */
    String[] store(MultipartFile file, String barcode);
}
