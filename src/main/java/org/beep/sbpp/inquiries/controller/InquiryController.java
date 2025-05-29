package org.beep.sbpp.inquiries.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.inquiries.dto.InquiryRequestDTO;
import org.beep.sbpp.inquiries.dto.InquiryResponseDTO;
import org.beep.sbpp.inquiries.service.InquiryService;
import org.beep.sbpp.util.UserInfoUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inquiries")
@Validated
@RequiredArgsConstructor
public class InquiryController {

    private final InquiryService inquiryService;
    private final UserInfoUtil userInfoUtil;

    @GetMapping
    public ResponseEntity<Page<InquiryResponseDTO>> list(
            @PageableDefault(page = 0, size = 10, sort = "regDate", direction = org.springframework.data.domain.Sort.Direction.DESC)
            Pageable pageable) {
        Page<InquiryResponseDTO> page = inquiryService.getInquiryList(pageable);
        return ResponseEntity.ok(page);
    }

    @PostMapping
    public ResponseEntity<InquiryResponseDTO> createInquiry(
            @RequestBody @Valid InquiryRequestDTO dto,
            HttpServletRequest request) {
        Long uid = userInfoUtil.getAuthUserId(request);
        InquiryResponseDTO result = inquiryService.createInquiry(dto, uid);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<InquiryResponseDTO> get(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long uid = userInfoUtil.getAuthUserId(request);
        return ResponseEntity.ok(inquiryService.getInquiry(id, uid));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InquiryResponseDTO> update(
            @PathVariable Long id,
            @RequestBody @Valid InquiryRequestDTO dto,
            HttpServletRequest request) {
        Long uid = userInfoUtil.getAuthUserId(request);
        return ResponseEntity.ok(inquiryService.updateInquiry(id, dto, uid));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            HttpServletRequest request) {
        Long uid = userInfoUtil.getAuthUserId(request);
        inquiryService.deleteInquiry(id, uid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/images")
    public ResponseEntity<Void> uploadImages(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files,
            HttpServletRequest request) {
        Long uid = userInfoUtil.getAuthUserId(request);
        inquiryService.uploadImages(id, uid, files);
        return ResponseEntity.ok().build();
    }
}
