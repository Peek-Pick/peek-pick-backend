package org.beep.sbpp.barcode.service;

import org.beep.sbpp.barcode.dto.ViewHistoryResponseDTO;
import org.beep.sbpp.reviews.dto.ReviewAddDTO;

import java.util.List;

public interface BarcodeService {
    List<ViewHistoryResponseDTO> getRecentBarcodeViewHistory(Long userId);
    void saveHistoryByBarcode(String barcode, Long userId);
    void updateIsReview(ReviewAddDTO reviewAddDTO);
    int countUnreviewedBarcodeHistory(Long userId);
}
