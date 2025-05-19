package org.beep.sbpp.admin.notice.storage;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class LocalImageStorageService implements ImageStorageService {

    /** application.properties 에 설정된 이미지 저장 경로 예: uploads/notices/ */
    @Value("${notice.image.upload-dir}")
    private String uploadDir;

    /** 애플리케이션이 외부에 노출할 base URL 예: http://localhost:8080/uploads/ */
    @Value("${notice.image.base-url}")
    private String baseUrl;

    @Override
    public String store(MultipartFile file) throws Exception {
        // 저장 디렉토리 존재 확인
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (Files.notExists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 고유 파일명 생성
        String ext = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID().toString() + (ext.isEmpty() ? "" : "." + ext);

        // 파일 저장
        Path target = uploadPath.resolve(filename);
        try (FileOutputStream fos = new FileOutputStream(target.toFile())) {
            fos.write(file.getBytes());
        }

        // 접근 가능한 URL 반환
        return baseUrl + filename;
    }

    private String getExtension(String original) {
        if (original == null) {
            return "";
        }
        int dot = original.lastIndexOf('.');
        return (dot == -1 ? "" : original.substring(dot + 1));
    }
}
