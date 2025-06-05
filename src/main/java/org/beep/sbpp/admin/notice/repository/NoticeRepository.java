package org.beep.sbpp.admin.notice.repository;

import org.beep.sbpp.admin.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 공지사항 저장소
 */
public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 필요 시 추가 쿼리 메서드 정의
}
