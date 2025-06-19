package org.beep.sbpp.admin.inquiries.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.admin.inquiries.dto.InquiryReplyResponseDTO;
import org.beep.sbpp.admin.inquiries.entities.InquiryReply;
import org.beep.sbpp.admin.inquiries.repository.InquiryReplyRepository;
import org.beep.sbpp.inquiries.controller.InquiryNotFoundException;
import org.beep.sbpp.inquiries.dto.InquiryResponseDTO;
import org.beep.sbpp.inquiries.entities.Inquiry;
import org.beep.sbpp.inquiries.entities.InquiryImage;
import org.beep.sbpp.inquiries.enums.InquiryStatus;
import org.beep.sbpp.inquiries.repository.InquiryRepository;
import org.beep.sbpp.users.entities.UserProfileEntity;
import org.beep.sbpp.users.repository.UserProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminInquiryServiceImpl implements AdminInquiryService {
    private final InquiryRepository inquiryRepository;
    private final InquiryReplyRepository inquiryReplyRepository;
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

        inquiry.setIsDelete(!inquiry.getIsDelete());  // 현재 값 반대로 토글
        inquiry.setModDate(LocalDateTime.now());

        inquiryRepository.save(inquiry);
    }

    @Override
    public void replyInquiry(Long inquiryId, String answerText) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new InquiryNotFoundException(inquiryId));

        if (inquiry.getIsDelete()) {
            throw new RuntimeException("삭제된 문의글에는 답변할 수 없습니다.");
        }

        if (inquiryReplyRepository.existsByInquiry(inquiry)) {
            throw new RuntimeException("이미 답변된 문의글입니다.");
        }

        InquiryReply answer = InquiryReply.builder()
                .inquiry(inquiry)
                .content(answerText)
                .build();

        inquiryReplyRepository.save(answer);

        // 문의 상태를 ANSWERED로 바꿈
        inquiry.setStatus(InquiryStatus.ANSWERED);
        inquiryRepository.save(inquiry);
    }

    @Override
    public void editReply(Long inquiryId, String newContent) {
        InquiryReply reply = inquiryReplyRepository.findByInquiry_InquiryId(inquiryId)
                .orElseThrow(() -> new RuntimeException("답변이 존재하지 않습니다. inquiryId=" + inquiryId));

        reply.setContent(newContent);

        inquiryReplyRepository.save(reply);
    }

    @Override
    @Transactional(readOnly = true)
    public InquiryReplyResponseDTO getReplyByInquiryId(Long inquiryId) {
        InquiryReply reply = inquiryReplyRepository.findByInquiry_InquiryId(inquiryId)
                .orElseThrow(() -> new RuntimeException("답변이 존재하지 않습니다. inquiryId=" + inquiryId));

        return InquiryReplyResponseDTO.builder()
                .content(reply.getContent())
                .regDate(reply.getRegDate())
                .build();
    }

    @Override
    public void deleteReply(Long inquiryId) {
        InquiryReply reply = inquiryReplyRepository.findByInquiry_InquiryId(inquiryId)
                .orElseThrow(() -> new RuntimeException("답변이 존재하지 않습니다. inquiryId=" + inquiryId));

        inquiryReplyRepository.delete(reply);

        // 문의 상태도 다시 변경(답변 삭제 시 상태를 '대기중'으로)
        Inquiry inquiry = reply.getInquiry();
        inquiry.setStatus(InquiryStatus.PENDING);
        inquiryRepository.save(inquiry);
    }
}
