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
    public ResponseEntity<String> scanAndSaveHistory(@PathVariable String barcode, HttpServletRequest request,
                                                     @RequestParam(required = false, defaultValue = "en") String lang) {
        Long userId = userInfoUtil.getAuthUserId(request);
        barcodeService.saveHistoryByBarcode(barcode, userId, lang);

        return ResponseEntity.ok(barcode);
    }

    /**
     * 최근 조회 이력 조회 (언어별 이름 포함)
     *
     * @param lang    조회할 언어 코드 (ko, en, ja). 없으면 ko 기본.
     */
    @GetMapping("/history")
    public ResponseEntity<List<ViewHistoryResponseDTO>> getRecentBarcodeViews(
            @RequestParam(name = "lang", required = false, defaultValue = "en") String lang,
            HttpServletRequest request
    ) {
        Long userId = userInfoUtil.getAuthUserId(request);
        List<ViewHistoryResponseDTO> historyList =
                barcodeService.getRecentBarcodeViewHistory(userId, lang);
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
