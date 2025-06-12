package org.beep.sbpp.admin.notice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * Nginx 루트 디렉터리 하위에 이미지 파일을 저장하고 접근 가능한 URL을 반환한다.
 */
@Service
@Transactional
public class AdminNoticeImageStorageServiceImpl implements AdminNoticeImageStorageService {

    /** application.properties (또는 yml) 에서 설정한 Nginx HTML 루트 디렉터리 경로 (예: C:/nginx-1.26.3) */
    @Value("${nginx.root-dir}")
    private String nginxRootDir;

    /** 서브 폴더 이름: nginx.root-dir/html/upload/notices */
    private static final String SERVICE_SUBFOLDER = "notices";

    /** 클라이언트가 접근할 때 쓰는 URL 접두어: "/upload/notices/" */
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

            // 고유한 파일명 생성 (UUID + 확장자)
            String filename = UUID.randomUUID().toString() + ext;

            // Nginx 루트 디렉터리 + 서브폴더 (예: C:/nginx/html/upload/notices)
            Path uploadPath = Paths.get(nginxRootDir, "upload", SERVICE_SUBFOLDER);
            if (Files.notExists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 실제 파일 저장
            Path target = uploadPath.resolve(filename);
            file.transferTo(target.toFile());

            // 클라이언트가 접근 가능한 URL만 반환
            return URL_PREFIX + filename;
        } catch (IOException e) {
            throw new RuntimeException("이미지 저장 실패", e);
        }
    }
}
