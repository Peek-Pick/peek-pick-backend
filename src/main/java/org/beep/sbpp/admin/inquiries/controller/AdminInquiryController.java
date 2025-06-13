package org.beep.sbpp.admin.inquiries.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.admin.inquiries.service.AdminInquiryService;
import org.beep.sbpp.inquiries.dto.InquiryResponseDTO;
import org.beep.sbpp.inquiries.service.InquiryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/inquiries")
@RequiredArgsConstructor
@Slf4j
public class AdminInquiryController {

    private final InquiryService inquiryService;
    private final AdminInquiryService adminInquiryService;

    @GetMapping
    public ResponseEntity<Page<InquiryResponseDTO>> list(
            @RequestParam(name = "includeDeleted", defaultValue = "true") boolean includeDeleted,
            @RequestParam(name = "category",       defaultValue = "all")    String  category,
            @RequestParam(name = "keyword",        defaultValue = "")       String  keyword,
            @RequestParam(name = "status",         defaultValue = "")       String  status,
            @PageableDefault(page = 0, size = 20, sort = "regDate", direction = Sort.Direction.DESC)
            Pageable pageable) {

        Page<InquiryResponseDTO> page = inquiryService.getFilteredInquiries(
                includeDeleted, category, keyword.trim(), status, pageable
        );
        return ResponseEntity.ok(page);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InquiryResponseDTO> getAdminInquiry( @PathVariable Long id ) {
        return ResponseEntity.ok(adminInquiryService.getAdminInquiry(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdminInquiry(@PathVariable Long id) {
        adminInquiryService.deleteAdminInquiry(id);
        return ResponseEntity.noContent().build();
    }
}
