package org.beep.sbpp.notices.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.beep.sbpp.admin.notice.entity.Notice;

/**
 * 공지사항 조회 전용 Repository
 */
public interface NoticeRepository extends JpaRepository<Notice, Long> {
}
