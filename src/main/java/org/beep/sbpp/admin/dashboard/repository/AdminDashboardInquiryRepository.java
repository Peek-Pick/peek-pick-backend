package org.beep.sbpp.admin.dashboard.repository;

import org.beep.sbpp.inquiries.entities.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminDashboardInquiryRepository extends JpaRepository<Inquiry, Long>{
}