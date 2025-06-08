package org.beep.sbpp.admin.notice.service;

import org.beep.sbpp.admin.notice.controller.NoticeNotFoundException;
import org.beep.sbpp.admin.notice.entity.Notice;
import org.beep.sbpp.admin.notice.entity.NoticeImage;
import org.beep.sbpp.admin.notice.dto.NoticeRequestDTO;
import org.beep.sbpp.admin.notice.dto.NoticeResponseDTO;
import org.beep.sbpp.admin.notice.repository.NoticeRepository;
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
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepo;
    private final ImageStorageService imageStorageService;

    public NoticeServiceImpl(NoticeRepository noticeRepo,
                             ImageStorageService imageStorageService) {
        this.noticeRepo = noticeRepo;
        this.imageStorageService = imageStorageService;
    }

    @Override
    public NoticeResponseDTO createNotice(NoticeRequestDTO dto) {
        Notice notice = Notice.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .build();

        // imgUrls 목록이 들어온 경우(기존에 담긴 URL이 있을 때)
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

        // 1) 제목·내용 업데이트
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());

        // 2) 이미지 목록은 dto.getImgUrls() 가 null 이면 SKIP
        if (dto.getImgUrls() != null) {
            List<String> existing = notice.getImages().stream()
                    .map(NoticeImage::getImgUrl)
                    .toList();
            List<String> requested = dto.getImgUrls();

            // 삭제: 컬렉션에서 빼면 orphanRemoval=true 에 의해 DB에서 삭제됨
            notice.getImages().removeIf(img ->
                    !requested.contains(img.getImgUrl())
            );

            // 추가: 새로 요청된 URL만 추가
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

    /**
     * 새로 추가한 이미지 업로드 메서드:
     * MultipartFile 리스트를 받아, ImageStorageService를 통해 Nginx에 저장하고
     * 반환된 URL 정보를 NoticeImage 엔티티로 생성하여 Notice에 추가 후 저장한다.
     */
    @Override
    public void uploadImages(Long noticeId, List<MultipartFile> files) {
        Notice notice = noticeRepo.findById(noticeId)
                .orElseThrow(() -> new NoticeNotFoundException(noticeId));

        // 파일 개수만큼 반복하면서 ImageStorageService.store() 호출
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            // Nginx 루트 아래에 파일 저장하고, 접근 가능한 URL 반환
            String url = imageStorageService.store(file);
            NoticeImage img = NoticeImage.builder()
                    .imgUrl(url)
                    .build();
            notice.addImage(img);
        }

        // cascade 설정에 의해 NoticeImage 엔티티도 함께 저장됨
        noticeRepo.save(notice);
    }
}
