package org.beep.sbpp.admin.notice.repository;

import org.beep.sbpp.admin.notice.entity.Notice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 공지사항 저장소
 */
public interface AdminNoticeRepository extends JpaRepository<Notice, Long> {

    /**
     * 제목에 keyword 포함된 공지 검색
     */
    Page<Notice> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

    /**
     * 내용에 keyword 포함된 공지 검색
     */
    Page<Notice> findByContentContainingIgnoreCase(String keyword, Pageable pageable);

    /**
     * 제목 또는 내용에 keyword 포함된 공지 검색
     */
    Page<Notice> findByTitleContainingIgnoreCaseOrContentContainingIgnoreCase(String kw1, String kw2, Pageable pageable);
}
