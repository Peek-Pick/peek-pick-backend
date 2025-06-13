package org.beep.sbpp.admin.inquiries.service;

import org.beep.sbpp.inquiries.dto.InquiryResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public interface AdminInquiryService {
    InquiryResponseDTO getAdminInquiry(Long id);
    void deleteAdminInquiry(Long id);
}
