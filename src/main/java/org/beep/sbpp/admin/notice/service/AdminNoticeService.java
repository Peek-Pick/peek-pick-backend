package org.beep.sbpp.admin.notice.service;

import org.beep.sbpp.admin.notice.dto.AdminNoticeRequestDTO;
import org.beep.sbpp.admin.notice.dto.AdminNoticeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 공지사항 관련 비즈니스 로직 인터페이스
 */
public interface AdminNoticeService {

    AdminNoticeResponseDTO createNotice(AdminNoticeRequestDTO dto);

    AdminNoticeResponseDTO updateNotice(Long id, AdminNoticeRequestDTO dto);

    void deleteNotice(Long id);

    AdminNoticeResponseDTO getNotice(Long id);

    /**
     * 페이징 및 검색 조건을 포함한 공지 목록 조회
     * @param pageable 페이징 정보
     * @param keyword 검색어 (nullable)
     * @param category 검색 기준: title, content, titleContent
     * @return Page<AdminNoticeResponseDTO>
     */
    Page<AdminNoticeResponseDTO> getNoticeList(Pageable pageable, String keyword, String category);

    /**
     * 이미지 업로드
     * @param noticeId 공지 ID
     * @param files 업로드할 MultipartFile 목록
     */
    void uploadImages(Long noticeId, List<MultipartFile> files);
}
