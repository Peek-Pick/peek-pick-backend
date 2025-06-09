package org.beep.sbpp.inquiries.repository;

import org.beep.sbpp.inquiries.dto.InquiryImgDTO;
import org.beep.sbpp.inquiries.entities.InquiryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InquiryImgRepository extends JpaRepository<InquiryImage, Long> {
    @Query("""
                select new org.beep.sbpp.inquiries.dto.InquiryImgDTO(ii.imageId, ii.inquiry.inquiryId, ii.imgUrl)
                from InquiryImage ii
                where ii.inquiry.inquiryId = :inquiryId
                order by ii.imageId asc
    """)
    List<InquiryImgDTO> selectImgAll(@Param("inquiryId") Long inquiryId);
}
