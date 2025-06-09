package org.beep.sbpp.admin.products.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

/**
 * AdminProductImageStorageService 구현체
 *  - {nginx.root-dir}/upload/products 에 파일 저장
 *  - "http://localhost/upload/products/{uuid.ext}" 절대 URL 반환
 */
@Service
@Transactional
public class AdminProductImageStorageServiceImpl
        implements AdminProductImageStorageService {

    /** application.yml 의 nginx.root-dir (예: C:/nginx-1.26.3/html) */
    @Value("${nginx.root-dir}")
    private String nginxRootDir;

    private static final String SERVICE_SUBFOLDER = "products";
    private static final String URL_PREFIX =
            "http://localhost/upload/" + SERVICE_SUBFOLDER + "/";

    @Override
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("빈 파일은 업로드할 수 없습니다.");
        }
        try {
            // 확장자 추출
            String original = file.getOriginalFilename();
            String ext = (original != null && original.contains("."))
                    ? original.substring(original.lastIndexOf('.'))
                    : "";
            // UUID 파일명
            String filename = UUID.randomUUID() + ext;

            // 저장 디렉터리: {nginxRootDir}/upload/products
            Path uploadDir = Paths.get(nginxRootDir, "upload", SERVICE_SUBFOLDER);
            if (Files.notExists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }
            // 파일 쓰기
            Path target = uploadDir.resolve(filename);
            file.transferTo(target.toFile());

            // 절대 URL 반환
            return URL_PREFIX + filename;
        } catch (IOException e) {
            throw new RuntimeException("상품 이미지 저장 실패", e);
        }
    }
}
