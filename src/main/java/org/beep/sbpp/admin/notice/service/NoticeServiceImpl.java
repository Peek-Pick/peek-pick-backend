package org.beep.sbpp.admin.notice.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.beep.sbpp.admin.notice.domain.Notice;
import org.beep.sbpp.admin.notice.domain.NoticeImage;
import org.beep.sbpp.admin.notice.dto.NoticeRequestDto;
import org.beep.sbpp.admin.notice.dto.NoticeResponseDto;
import org.beep.sbpp.admin.notice.repository.NoticeRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepo;

    public NoticeServiceImpl(NoticeRepository noticeRepo) {
        this.noticeRepo = noticeRepo;
    }

    @Override
    public NoticeResponseDto createNotice(NoticeRequestDto dto) {
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
    public NoticeResponseDto updateNotice(Long id, NoticeRequestDto dto) {
        Notice notice = noticeRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("공지 없음: " + id));

        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());

        List<String> existing = notice.getImages().stream()
                .map(NoticeImage::getImgUrl)
                .collect(Collectors.toList());

        List<String> requested = dto.getImgUrls() != null
                ? dto.getImgUrls() : Collections.emptyList();

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
    public NoticeResponseDto getNotice(Long id) {
        return noticeRepo.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("공지 없음: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NoticeResponseDto> getNoticeList(Pageable pageable) {
        return noticeRepo.findAll(pageable)
                .map(this::toDto);
    }

    private NoticeResponseDto toDto(Notice n) {
        return NoticeResponseDto.builder()
                .noticeId(n.getNoticeId())
                .title(n.getTitle())
                .content(n.getContent())
                .regDate(n.getRegDate())
                .modDate(n.getModDate())
                .imgUrls(n.getImages().stream()
                        .map(NoticeImage::getImgUrl)
                        .collect(Collectors.toList()))
                .build();
    }
}
