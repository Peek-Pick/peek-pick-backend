package org.beep.sbpp.admin.notice.repository;

import org.beep.sbpp.admin.notice.domain.Notice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    // 추가 쿼리 메서드 필요 시 여기에 선언
}