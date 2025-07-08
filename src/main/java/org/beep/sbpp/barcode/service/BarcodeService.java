package org.beep.sbpp.barcode.service;

import org.beep.sbpp.barcode.dto.ViewHistoryResponseDTO;
import org.beep.sbpp.reviews.dto.ReviewAddDTO;

import java.util.List;

/**
 * 바코드 기반 조회 기록 및 리뷰 상태 업데이트 서비스
 */
public interface BarcodeService {
    /**
     * 바코드를 스캔한 기록을 저장
     */
    void saveHistoryByBarcode(String barcode, Long userId, String lang);

    /**
     * 최근 조회 기록을 언어별 이름과 함께 조회
     * @param userId 조회 사용자 ID
     * @param lang   "ko"|"en"|"ja"
     */
    List<ViewHistoryResponseDTO> getRecentBarcodeViewHistory(Long userId, String lang);

    /**
     * 리뷰가 등록되면 isReview 플래그를 true 로 업데이트
     */
    void updateIsReview(ReviewAddDTO reviewAddDTO);

    /**
     * 아직 리뷰 작성되지 않은 바코드 기록 수 집계 (최대 20)
     */
    int countUnreviewedBarcodeHistory(Long userId);
}
