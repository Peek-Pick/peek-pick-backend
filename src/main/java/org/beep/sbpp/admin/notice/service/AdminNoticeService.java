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

    Page<AdminNoticeResponseDTO> getNoticeList(Pageable pageable);

    /**
     * 이미지 업로드
     * @param noticeId 공지 ID
     * @param files 업로드할 MultipartFile 목록
     */
    void uploadImages(Long noticeId, List<MultipartFile> files);
}
