package org.beep.sbpp.admin.inquiries.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.inquiries.controller.InquiryNotFoundException;
import org.beep.sbpp.inquiries.dto.InquiryResponseDTO;
import org.beep.sbpp.inquiries.entities.Inquiry;
import org.beep.sbpp.inquiries.entities.InquiryImage;
import org.beep.sbpp.inquiries.repository.InquiryRepository;
import org.beep.sbpp.users.entities.UserProfileEntity;
import org.beep.sbpp.users.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminInquiryServiceImpl implements AdminInquiryService {
    private final InquiryRepository inquiryRepository;
    private final UserProfileRepository userProfileRepository;

    private InquiryResponseDTO toDto(Inquiry n) {

        Long uid = n.getUserEntity().getUserId();

        UserProfileEntity userProfile = userProfileRepository.findByUserId(uid)
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 사용자입니다."));

        return InquiryResponseDTO.builder()
                .inquiryId(n.getInquiryId())
                .userId(uid)
                .userEmail(n.getUserEntity().getEmail())
                .userNickname(userProfile.getNickname())
                .userProfileImgUrl(userProfile.getProfileImgUrl())
                .title(n.getTitle())
                .content(n.getContent())
                .type(n.getType())
                .status(n.getStatus())
                .isDelete(n.getIsDelete())
                .regDate(n.getRegDate())
                .modDate(n.getModDate())
                .imgUrls(n.getImages().stream()
                        .map(InquiryImage::getImgUrl)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public InquiryResponseDTO getAdminInquiry(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new InquiryNotFoundException(id));

        return toDto(inquiry);
    }

    @Override
    public void deleteAdminInquiry(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("문의 없음: " + id));

        inquiry.setIsDelete(true);      // soft delete

        inquiryRepository.save(inquiry);
    }
}
