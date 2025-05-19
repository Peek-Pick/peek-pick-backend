package org.beep.sbpp.admin.notice.service;

import java.util.List;

import org.beep.sbpp.admin.notice.dto.NoticeRequestDTO;
import org.beep.sbpp.admin.notice.dto.NoticeResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface NoticeService {
    Page<NoticeResponseDTO> getNoticeList(Pageable pageable);
    NoticeResponseDTO createNotice(NoticeRequestDTO dto);
    NoticeResponseDTO getNotice(Long id);
    NoticeResponseDTO updateNotice(Long id, NoticeRequestDTO dto);
    void deleteNotice(Long id);

    // 새로 추가한 메서드: MultipartFile 리스트를 받아 해당 공지에 이미지 업로드
    void uploadImages(Long noticeId, List<MultipartFile> files);
}
