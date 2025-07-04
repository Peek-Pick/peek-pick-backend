package org.beep.sbpp.barcode.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.barcode.dto.ViewHistoryResponseDTO;
import org.beep.sbpp.barcode.service.BarcodeService;
import org.beep.sbpp.util.UserInfoUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/barcode")
@RequiredArgsConstructor
@Slf4j
public class BarcodeController {

    private final BarcodeService barcodeService;
    private final UserInfoUtil userInfoUtil;

    @PostMapping("/scan/{barcode}")
    public ResponseEntity<String> scanAndSaveHistory(@PathVariable String barcode, HttpServletRequest request) {
        Long userId = userInfoUtil.getAuthUserId(request);
        barcodeService.saveHistoryByBarcode(barcode, userId);

        return ResponseEntity.ok(barcode);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ViewHistoryResponseDTO>> getRecentBarcodeViews(HttpServletRequest request) {
        Long userId = userInfoUtil.getAuthUserId(request);
        List<ViewHistoryResponseDTO> historyList = barcodeService.getRecentBarcodeViewHistory(userId);
        return ResponseEntity.ok(historyList);
    }

    @GetMapping("/history/count")
    public ResponseEntity<Integer> getUnreviewedHistoryCount(HttpServletRequest request) {
        Long userId = userInfoUtil.getAuthUserId(request);
        int count = barcodeService.countUnreviewedBarcodeHistory(userId);
        return ResponseEntity.ok(count);
    }

    @GetMapping
    public ResponseEntity<String> nullBarcode() {
        return ResponseEntity.ok("Barcode endpoint is alive");
    }
}
