package org.beep.sbpp.inquiries.repository;

import org.beep.sbpp.inquiries.entities.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    // 추가 쿼리 메서드 필요 시 여기에 선언
}