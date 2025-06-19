package org.beep.sbpp.admin.notice.service;

import org.beep.sbpp.admin.notice.controller.AdminNoticeNotFoundException;
import org.beep.sbpp.admin.notice.entity.Notice;
import org.beep.sbpp.admin.notice.entity.NoticeImage;
import org.beep.sbpp.admin.notice.dto.AdminNoticeRequestDTO;
import org.beep.sbpp.admin.notice.dto.AdminNoticeResponseDTO;
import org.beep.sbpp.admin.notice.repository.AdminNoticeRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * NoticeService 구현체: 공지 생성/수정/삭제/조회 및 이미지 업로드 로직
 */
@Service
@Transactional
public class AdminNoticeServiceImpl implements AdminNoticeService {

    private final AdminNoticeRepository noticeRepo;
    private final AdminNoticeImageStorageService imageStorageService;

    public AdminNoticeServiceImpl(AdminNoticeRepository noticeRepo,
                                  AdminNoticeImageStorageService imageStorageService) {
        this.noticeRepo = noticeRepo;
        this.imageStorageService = imageStorageService;
    }

    @Override
    public AdminNoticeResponseDTO createNotice(AdminNoticeRequestDTO dto) {
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
    public AdminNoticeResponseDTO updateNotice(Long id, AdminNoticeRequestDTO dto) {
        Notice notice = noticeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("공지 없음: " + id));

        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());

        if (dto.getImgUrls() != null) {
            List<String> existing = notice.getImages().stream()
                    .map(NoticeImage::getImgUrl)
                    .toList();
            List<String> requested = dto.getImgUrls();

            notice.getImages().removeIf(img ->
                    !requested.contains(img.getImgUrl())
            );

            requested.stream()
                    .filter(url -> !existing.contains(url))
                    .forEach(url -> {
                        NoticeImage img = NoticeImage.builder()
                                .imgUrl(url)
                                .build();
                        notice.addImage(img);
                    });
        }

        Notice updated = noticeRepo.save(notice);
        return toDto(updated);
    }

    @Override
    public void deleteNotice(Long id) {
        noticeRepo.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public AdminNoticeResponseDTO getNotice(Long id) {
        return noticeRepo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("공지 없음: " + id));
    }

    /**
     * 페이징 및 검색 조건을 포함한 공지 목록 조회
     * - keyword 가 null이면 전체 목록
     * - category 는 title / content / titleContent
     */
    @Override
    @Transactional(readOnly = true)
    public Page<AdminNoticeResponseDTO> getNoticeList(Pageable pageable, String keyword, String category) {
        Page<Notice> result;

        if (keyword == null || keyword.isBlank()) {
            result = noticeRepo.findAll(pageable);
        } else {
            String kw = keyword.trim();
            switch (category) {
                case "content":
                    result = noticeRepo.findByContentContainingIgnoreCase(kw, pageable);
                    break;
                case "titleContent":
                    result = noticeRepo.findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(kw, kw, pageable);
                    break;
                case "title":
                default:
                    result = noticeRepo.findByTitleContainingIgnoreCase(kw, pageable);
                    break;
            }
        }

        return result.map(this::toDto);
    }

    private AdminNoticeResponseDTO toDto(Notice n) {
        return AdminNoticeResponseDTO.builder()
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

    @Override
    public void uploadImages(Long noticeId, List<MultipartFile> files) {
        Notice notice = noticeRepo.findById(noticeId)
                .orElseThrow(() -> new AdminNoticeNotFoundException(noticeId));

        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            String url = imageStorageService.store(file);
            NoticeImage img = NoticeImage.builder()
                    .imgUrl(url)
                    .build();
            notice.addImage(img);
        }

        noticeRepo.save(notice);
    }
}
