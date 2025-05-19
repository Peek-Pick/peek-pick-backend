package org.beep.sbpp.admin.notice.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.beep.sbpp.admin.notice.controller.NoticeNotFoundException;
import org.beep.sbpp.admin.notice.domain.Notice;
import org.beep.sbpp.admin.notice.domain.NoticeImage;
import org.beep.sbpp.admin.notice.dto.NoticeRequestDTO;
import org.beep.sbpp.admin.notice.dto.NoticeResponseDTO;
import org.beep.sbpp.admin.notice.repository.NoticeRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepo;

    public NoticeServiceImpl(NoticeRepository noticeRepo) {
        this.noticeRepo = noticeRepo;
    }

    @Override
    public NoticeResponseDTO createNotice(NoticeRequestDTO dto) {
        Notice notice = Notice.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();

        if (dto.getImgUrls() != null) {
            dto.getImgUrls().forEach(url -> {
                NoticeImage img = NoticeImage.builder()
                        .imgUrl(url)
                        .build();
                notice.addImage(img);
            });
        }

        Notice saved = noticeRepo.save(notice);
        return toDto(saved);
    }

    @Override
    public NoticeResponseDTO updateNotice(Long id, NoticeRequestDTO dto) {
        Notice notice = noticeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("공지 없음: " + id));

        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());

        // 기존 이미지 URL
        List<String> existing = notice.getImages().stream()
                .map(NoticeImage::getImgUrl)
                .toList();

        // 요청된 이미지 URL
        List<String> requested = dto.getImgUrls() != null
                ? dto.getImgUrls() : List.of();

        // 삭제
        notice.getImages().removeIf(img -> !requested.contains(img.getImgUrl()));

        // 추가
        requested.stream()
                .filter(url -> !existing.contains(url))
                .forEach(url -> {
                    NoticeImage img = NoticeImage.builder()
                            .imgUrl(url)
                            .build();
                    notice.addImage(img);
                });

        Notice updated = noticeRepo.save(notice);
        return toDto(updated);
    }

    @Override
    public void deleteNotice(Long id) {
        noticeRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public NoticeResponseDTO getNotice(Long id) {
        return noticeRepo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("공지 없음: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoticeResponseDTO> getNoticeList(Pageable pageable) {
        return noticeRepo.findAll(pageable)
                .map(this::toDto);
    }

    private NoticeResponseDTO toDto(Notice n) {
        return NoticeResponseDTO.builder()
                .noticeId(n.getNoticeId())
                .title(n.getTitle())
                .content(n.getContent())
                .regDate(n.getRegDate())
                .modDate(n.getModDate())
                .imgUrls(n.getImages().stream()
                        .map(NoticeImage::getImgUrl)
                        .toList())
                .build();
    }

    // 새로 추가한 메서드: 실제 파일을 저장하고 NoticeImage 엔티티로 연결
    @Value("${notice.image.upload-dir}")
    private String uploadDir;

    @Override
    public void uploadImages(Long noticeId, List<MultipartFile> files) {
        Notice notice = noticeRepo.findById(noticeId)
                .orElseThrow(() -> new NoticeNotFoundException(noticeId));

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                throw new RuntimeException("업로드 디렉토리 생성 실패", e);
            }
        }

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
            Path dest = uploadPath.resolve(filename);
            try {
                file.transferTo(dest.toFile());
            } catch (IOException e) {
                throw new RuntimeException("파일 저장 실패: " + filename, e);
            }
            NoticeImage img = NoticeImage.builder()
                    .imgUrl("/uploads/" + filename)
                    .build();
            notice.addImage(img);
        }
        // 이미지가 추가된 notice를 저장 (cascade 설정 필요)
        noticeRepo.save(notice);
    }
}
