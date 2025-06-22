package org.beep.sbpp.admin.products.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * AdminProductImageStorageService 구현체
 *  - {nginx.root-dir}/products 에 원본 저장
 *  - {nginx.root-dir}/product_thumbnail 에 썸네일 저장
 *  - URL은 "/products/...", "/product_thumbnail/..." 형태로 반환
 */
@Service
@Transactional
public class AdminProductImageStorageServiceImpl implements AdminProductImageStorageService {

    @Value("${nginx.root-dir}")
    private String nginxRootDir;

    private static final String ORIGINAL_DIR = "products";
    private static final String THUMB_DIR = "product_thumbnail";
    private static final int THUMB_SIZE = 300;

    @Override
    public String[] store(MultipartFile file, String barcode) {
        if (file.isEmpty()) {
            throw new RuntimeException("빈 파일은 업로드할 수 없습니다.");
        }

        try {
            String originalName = file.getOriginalFilename();
            String ext = (originalName != null && originalName.contains("."))
                    ? originalName.substring(originalName.lastIndexOf('.')).toLowerCase()
                    : "";

            // 파일명 지정
            String filename = "pp-" + barcode + ext;
            String thumbFilename = "pp-" + barcode + "-thumb" + ext;

            // 저장 경로
            File originalPath = new File(nginxRootDir + "/" + ORIGINAL_DIR, filename);
            File thumbPath = new File(nginxRootDir + "/" + THUMB_DIR, thumbFilename);

            // 디렉토리 생성
            originalPath.getParentFile().mkdirs();
            thumbPath.getParentFile().mkdirs();

            // 원본 저장
            file.transferTo(originalPath);

            // 썸네일 저장 조건부
            String thumbUrl = null;
            BufferedImage originalImage = ImageIO.read(originalPath);
            if (originalImage.getWidth() >= THUMB_SIZE && originalImage.getHeight() >= THUMB_SIZE) {
                BufferedImage resized = resizeImage(originalImage, THUMB_SIZE, THUMB_SIZE);
                ImageIO.write(resized, ext.replace(".", ""), thumbPath);
                thumbUrl = "/" + THUMB_DIR + "/" + thumbFilename;
            }

            return new String[]{
                    "/" + ORIGINAL_DIR + "/" + filename,
                    thumbUrl
            };
        } catch (IOException e) {
            throw new RuntimeException("상품 이미지 저장 실패", e);
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int width, int height) {
        Image tmp = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return resized;
    }
}
