package org.beep.sbpp.inquiries.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Transactional
public class InquiryImageStorageServiceImpl implements InquiryImageStorageService{

    @Value("${nginx.root-dir}")
    private String nginxRootDir;
    private static final String SERVICE_SUBFOLDER = "inquiries";
    private static final String URL_PREFIX = "/upload/" + SERVICE_SUBFOLDER + "/";

    @Override
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("빈 파일은 업로드할 수 없습니다.");
        }

        try {
            // 원본 파일명에서 확장자 추출
            String original = file.getOriginalFilename();
            String ext = (original != null && original.contains("."))
                    ? original.substring(original.lastIndexOf("."))
                    : "";
            // 고유 파일명 생성
            String filename = UUID.randomUUID().toString() + ext;

            // Nginx 루트 + 서비스 폴더 조합 (예: C:/nginx-1.26.3/html/inquiries/)
            Path uploadPath = Paths.get(nginxRootDir, SERVICE_SUBFOLDER);
            if (Files.notExists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 실제 파일 저장
            Path target = uploadPath.resolve(filename);
            file.transferTo(target.toFile());

            // 변경: 클라이언트가 접근할 URL만 반환
            return URL_PREFIX + filename;
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패", e);
        }
    }
}