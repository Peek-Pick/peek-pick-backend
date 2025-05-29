package org.beep.sbpp.inquiries.service;

import lombok.RequiredArgsConstructor;
import org.beep.sbpp.inquiries.controller.InquiryNotFoundException;
import org.beep.sbpp.inquiries.dto.InquiryRequestDTO;
import org.beep.sbpp.inquiries.dto.InquiryResponseDTO;
import org.beep.sbpp.inquiries.entities.Inquiry;
import org.beep.sbpp.inquiries.entities.InquiryImage;
import org.beep.sbpp.inquiries.enums.InquiryStatus;
import org.beep.sbpp.inquiries.repository.InquiryRepository;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;

    @Override
    public InquiryResponseDTO createInquiry(InquiryRequestDTO dto, Long uid) {
        UserEntity user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("사용자 정보를 찾을 수 없습니다."));

        Inquiry inquiry = Inquiry.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .userEntity(user)
                .type(dto.getType())
                .status(InquiryStatus.PENDING)
                .build();

        if (dto.getImgUrls() != null) {
            dto.getImgUrls().forEach(url -> {
                InquiryImage img = InquiryImage.builder()
                        .imgUrl(url)
                        .build();
                inquiry.addImage(img);
            });
        }

        Inquiry saved = inquiryRepository.save(inquiry);
        return toDto(saved);
    }

    @Override
    public InquiryResponseDTO updateInquiry(Long id, InquiryRequestDTO dto, Long uid) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의 없음: " + id));

        UserEntity user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("사용자 정보 없음"));

        // 관리자거나 본인이면 허용
        if (!user.isAdmin() && !inquiry.getUserEntity().getUserId().equals(uid)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        inquiry.setTitle(dto.getTitle());
        inquiry.setContent(dto.getContent());
        inquiry.setType(dto.getType());
        inquiry.setStatus(dto.getStatus());

        List<String> existing = inquiry.getImages().stream()
                .map(InquiryImage::getImgUrl)
                .toList();

        List<String> requested = dto.getImgUrls() != null ? dto.getImgUrls() : List.of();

        inquiry.getImages().removeIf(img -> !requested.contains(img.getImgUrl()));
        requested.stream()
                .filter(url -> !existing.contains(url))
                .forEach(url -> {
                    InquiryImage img = InquiryImage.builder()
                            .imgUrl(url)
                            .build();
                    inquiry.addImage(img);
                });

        Inquiry updated = inquiryRepository.save(inquiry);
        return toDto(updated);
    }

    @Override
    public void deleteInquiry(Long id, Long uid) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의 없음: " + id));

        UserEntity user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("사용자 정보 없음"));

        // 관리자거나 본인이면 허용
        if (!user.isAdmin() && !inquiry.getUserEntity().getUserId().equals(uid)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        inquiryRepository.delete(inquiry);
    }

    @Override
    @Transactional(readOnly = true)
    public InquiryResponseDTO getInquiry(Long id, Long uid) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new InquiryNotFoundException(id));

        UserEntity user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("사용자 정보 없음"));

        // 관리자거나 본인이면 허용
        if (!user.isAdmin() && !inquiry.getUserEntity().getUserId().equals(uid)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        return toDto(inquiry);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InquiryResponseDTO> getInquiryList(Pageable pageable) {
        return inquiryRepository.findAll(pageable)
                .map(this::toDto);
    }

    private InquiryResponseDTO toDto(Inquiry n) {
        return InquiryResponseDTO.builder()
                .inquiryId(n.getInquiryId())
                .userId(n.getUserEntity() != null ? n.getUserEntity().getUserId() : null) // 사용자 연관 시 추가
                .title(n.getTitle())
                .content(n.getContent())
                .type(n.getType())
                .status(n.getStatus())
                .regDate(n.getRegDate())
                .modDate(n.getModDate())
                .imgUrls(n.getImages().stream()
                        .map(InquiryImage::getImgUrl)
                        .toList())
                .build();
    }

    @Value("${inquiries.image.upload-dir}")
    private String uploadDir;

    @Override
    public void uploadImages(Long inquiryId, Long uid, List<MultipartFile> files) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new InquiryNotFoundException(inquiryId));

        UserEntity user = userRepository.findById(uid)
                .orElseThrow(() -> new RuntimeException("사용자 정보 없음"));

        // 관리자거나 본인이면 허용
        if (!user.isAdmin() && !inquiry.getUserEntity().getUserId().equals(uid)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        Path uploadPath = Paths.get(uploadDir);
        try {
            Files.createDirectories(uploadPath);
        } catch (IOException e) {
            throw new RuntimeException("업로드 디렉토리 생성 실패", e);
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

            InquiryImage img = InquiryImage.builder()
                    .imgUrl("/uploads/" + filename)
                    .build();
            inquiry.addImage(img);
        }

        inquiryRepository.save(inquiry);
    }
}
