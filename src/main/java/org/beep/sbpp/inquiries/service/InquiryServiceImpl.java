package org.beep.sbpp.inquiries.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.inquiries.controller.InquiryNotFoundException;
import org.beep.sbpp.inquiries.dto.InquiryRequestDTO;
import org.beep.sbpp.inquiries.dto.InquiryResponseDTO;
import org.beep.sbpp.inquiries.entities.Inquiry;
import org.beep.sbpp.inquiries.entities.InquiryImage;
import org.beep.sbpp.inquiries.enums.InquiryStatus;
import org.beep.sbpp.inquiries.repository.InquiryRepository;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;
    private final UserRepository userRepository;
    private InquiryResponseDTO toDto(Inquiry n) {
        return InquiryResponseDTO.builder()
                .inquiryId(n.getInquiryId())
                .userId(n.getUserEntity().getUserId())
                .title(n.getTitle())
                .content(n.getContent())
                .type(n.getType())
                .status(n.getStatus())
                .regDate(n.getRegDate())
                .modDate(n.getModDate())
                .imgUrls(n.getImages().stream()
                        .map(InquiryImage::getImgUrl)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public InquiryResponseDTO createInquiry(InquiryRequestDTO dto, Long uid) {
        UserEntity user = userRepository.findById(uid)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        Inquiry inquiry = Inquiry.builder()
                .userEntity(user)
                .type(dto.getType())
                .title(dto.getTitle())
                .content(dto.getContent())
                .status(InquiryStatus.PENDING)
                .build();

        inquiryRepository.save(inquiry);
        return toDto(inquiry);
    }

    @Override
    public InquiryResponseDTO updateInquiry(Long id, InquiryRequestDTO dto, Long uid) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의 없음: " + id));

        if (!inquiry.getUserEntity().getUserId().equals(uid)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        inquiry.setTitle(dto.getTitle());
        inquiry.setContent(dto.getContent());
        inquiry.setType(dto.getType());

        inquiryRepository.save(inquiry);
        return toDto(inquiry);
    }

    @Override
    public void deleteInquiry(Long id, Long uid) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의 없음: " + id));

        if (!inquiry.getUserEntity().getUserId().equals(uid)) {
            throw new RuntimeException("권한이 없습니다.");
        }
        inquiryRepository.delete(inquiry);
    }

    @Override
    @Transactional(readOnly = true)
    public InquiryResponseDTO getInquiry(Long id, Long uid) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new InquiryNotFoundException(id));

        if (!inquiry.getUserEntity().getUserId().equals(uid)) {
            throw new RuntimeException("권한이 없습니다.");
        }
        return toDto(inquiry);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InquiryResponseDTO> getInquiryList(Pageable pageable) {
        return inquiryRepository.findAll(pageable).map(this::toDto);
    }

    @Override
    public void addImageUrls(Long inquiryId, Long uid, List<String> urls) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new InquiryNotFoundException(inquiryId));

        if (!inquiry.getUserEntity().getUserId().equals(uid)) {
            throw new RuntimeException("권한이 없습니다.");
        }

        // 기존 URL은 유지, 신규 URL만 추가
        List<String> existing = inquiry.getImages().stream()
                .map(InquiryImage::getImgUrl)
                .collect(Collectors.toList());

        urls.stream()
                .filter(url -> !existing.contains(url))
                .forEach(url -> {
                    InquiryImage img = InquiryImage.builder()
                            .imgUrl(url)
                            .build();
                    inquiry.addImage(img);
                });

        inquiryRepository.save(inquiry);
    }
}