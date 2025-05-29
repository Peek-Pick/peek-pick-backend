package org.beep.sbpp.barcode.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.barcode.service.BarcodeService;
import org.beep.sbpp.util.UserInfoUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/barcode")
@RequiredArgsConstructor
public class BarcodeController {

    private final BarcodeService barcodeService;
    private final UserInfoUtil userInfoUtil;

    @PostMapping("/scan/{barcode}")
    public ResponseEntity<String> scanAndSaveHistory(@PathVariable String barcode, HttpServletRequest request) {
        Long userId = userInfoUtil.getAuthUserId(request);
        barcodeService.saveHistoryByBarcode(barcode, userId);

        return ResponseEntity.ok(barcode);
    }
}
