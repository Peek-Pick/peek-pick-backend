package org.beep.sbpp.inquiries.repository;

import org.beep.sbpp.inquiries.entities.Inquiry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InquiryRepository extends JpaRepository<Inquiry, Long>, JpaSpecificationExecutor<Inquiry> {
    Page<Inquiry> findByIsDeleteFalse(Pageable pageable);
}