package org.beep.sbpp.inquiries.service;

import org.beep.sbpp.inquiries.dto.InquiryRequestDTO;
import org.beep.sbpp.inquiries.dto.InquiryResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface InquiryService {
    Page<InquiryResponseDTO> getInquiryList(Pageable pageable);
    InquiryResponseDTO createInquiry(InquiryRequestDTO dto, Long uid);
    InquiryResponseDTO getInquiry(Long id, Long uid);
    InquiryResponseDTO updateInquiry(Long id, InquiryRequestDTO dto, Long uid);
    void deleteInquiry(Long id, Long uid);

    // 새로 추가한 메서드: MultipartFile 리스트를 받아 해당 공지에 이미지 업로드
    void uploadImages(Long inquiryId, Long uid, List<MultipartFile> files);
}
