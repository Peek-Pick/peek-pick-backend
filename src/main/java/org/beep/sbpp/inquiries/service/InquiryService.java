package org.beep.sbpp.inquiries.service;

import org.beep.sbpp.inquiries.dto.InquiryRequestDTO;
import org.beep.sbpp.inquiries.dto.InquiryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public interface InquiryService {
    Page<InquiryResponseDTO> getInquiryList(Pageable pageable);
    InquiryResponseDTO createInquiry(InquiryRequestDTO dto, Long uid);
    InquiryResponseDTO getInquiry(Long id, Long uid);
    InquiryResponseDTO updateInquiry(Long id, InquiryRequestDTO dto, Long uid);
    void deleteInquiry(Long id, Long uid);
    void addImageUrls(Long inquiryId, Long uid, List<String> urls);
    void deleteImages(Long inquiryId, Long uid, List<String> urls);
    Page<InquiryResponseDTO> getFilteredInquiries(
            boolean includeDeleted,
            String category,
            String keyword,
            String status,
            Pageable pageable
    );
}
