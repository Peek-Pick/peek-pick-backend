package org.beep.sbpp.inquiries.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.admin.inquiries.dto.InquiryReplyResponseDTO;
import org.beep.sbpp.admin.inquiries.service.AdminInquiryService;
import org.beep.sbpp.inquiries.dto.DeleteImageRequestDTO;
import org.beep.sbpp.inquiries.dto.InquiryRequestDTO;
import org.beep.sbpp.inquiries.dto.InquiryResponseDTO;
import org.beep.sbpp.inquiries.service.InquiryImageStorageService;
import org.beep.sbpp.inquiries.service.InquiryService;
import org.beep.sbpp.util.UserInfoUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/inquiries")
@Validated
@RequiredArgsConstructor
@Slf4j
public class InquiryController {

    private final InquiryService inquiryService;
    private final InquiryImageStorageService storageService;
    private final UserInfoUtil userInfoUtil;
    private final AdminInquiryService adminInquiryService;

    @GetMapping()
    public ResponseEntity<Page<InquiryResponseDTO>> list(
            HttpServletRequest request,
            @PageableDefault(page = 0, size = 5, sort = "regDate", direction = Sort.Direction.DESC)
            Pageable pageable) {
        Long uid = userInfoUtil.getAuthUserId(request);
        Page<InquiryResponseDTO> page = inquiryService.getInquiryListByUser(uid, pageable);
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

        List<String> urls = files.stream()
                .map(file -> {
                    try {
                        return storageService.store(file);
                    } catch (Exception e) {
                        log.error("파일 업로드 실패", e);
                        throw new RuntimeException("이미지 업로드 실패");
                    }
                })
                .collect(Collectors.toList());

        inquiryService.addImageUrls(id, uid, urls);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/images")
    public ResponseEntity<Void> deleteImages(
            @PathVariable Long id,
            @RequestBody DeleteImageRequestDTO dto,
            HttpServletRequest request) {
        Long uid = userInfoUtil.getAuthUserId(request);
        inquiryService.deleteImages(id, uid, dto.getUrls());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/email")
    public ResponseEntity<String> getUserEmail(HttpServletRequest request) {
        String email = userInfoUtil.getAuthUserEmail(request);
        if (email == null) {
            return ResponseEntity.status(401).body("Unauthorized");
        }
        return ResponseEntity.ok(email);
    }

    @GetMapping("/{id}/reply")
    public ResponseEntity<InquiryReplyResponseDTO> getReply(@PathVariable Long id) {
        InquiryReplyResponseDTO dto = adminInquiryService.getReplyByInquiryId(id);
        return ResponseEntity.ok(dto);
    }
}
