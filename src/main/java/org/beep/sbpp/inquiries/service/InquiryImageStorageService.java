package org.beep.sbpp.inquiries.service;

import org.springframework.web.multipart.MultipartFile;

public interface InquiryImageStorageService {
    String store(MultipartFile file);
}
