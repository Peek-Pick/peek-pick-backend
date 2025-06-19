package org.beep.sbpp.admin.inquiries.service;

import org.beep.sbpp.admin.inquiries.dto.InquiryReplyResponseDTO;
import org.beep.sbpp.inquiries.dto.InquiryResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface AdminInquiryService {
    InquiryResponseDTO getAdminInquiry(Long id);
    void deleteAdminInquiry(Long id);
    void replyInquiry(Long inquiryId, String answerText);
    void editReply(Long inquiryId, String newContent);
    InquiryReplyResponseDTO getReplyByInquiryId(Long inquiryId);
    void deleteReply(Long inquiryId);
}
