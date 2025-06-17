package org.beep.sbpp.admin.inquiries.repository;

import org.beep.sbpp.admin.inquiries.entities.InquiryReply;
import org.beep.sbpp.inquiries.entities.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InquiryReplyRepository extends JpaRepository<InquiryReply, Long> {
    boolean existsByInquiry(Inquiry inquiry);
    Optional<InquiryReply> findByInquiry_InquiryId(Long inquiryId);
}
