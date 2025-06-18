package org.beep.sbpp.barcode.repository;

import org.beep.sbpp.barcode.entities.BarcodeHistoryEntity;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BarcodeHistoryRepository extends JpaRepository<BarcodeHistoryEntity, Long> {

    // 사용자별 최근 바코드 스캔 기록 (중복 상품 제거 후 최신순)
    @Query("""
    SELECT bh
    FROM BarcodeHistoryEntity bh
    WHERE bh.viewId IN (
        SELECT MAX(bh2.viewId)
        FROM BarcodeHistoryEntity bh2
        WHERE bh2.userId = :userId
        GROUP BY bh2.productId
    )
    ORDER BY bh.regDate DESC
    """)
    List<BarcodeHistoryEntity> findRecentDistinctByUser(
            @Param("userId") Long userId,
            Pageable pageable
    );

    // 특정 사용자+상품의 최신 기록 조회
    List<BarcodeHistoryEntity> findTopByUserIdAndProductIdOrderByRegDateDesc(
            Long userId, Long productId, Pageable pageable
    );

    // 최신 기록의 리뷰 여부 업데이트
    @Modifying
    @Transactional
    @Query("""
        UPDATE BarcodeHistoryEntity bh
        SET bh.isReview = :isReview
        WHERE bh.viewId = (
            SELECT bh2.viewId FROM BarcodeHistoryEntity bh2
            WHERE bh2.userId = :userId
              AND bh2.productId = :productId
            ORDER BY bh2.regDate DESC
            LIMIT 1
        )
    """)
    int updateIsReviewForLatestBarcodeHistory(
            @Param("userId") Long userId,
            @Param("productId") Long productId,
            @Param("isReview") Boolean isReview
    );

    // 리뷰 작성 안된 바코드 기록 개수 조회
    int countByUserIdAndIsReviewFalse(Long userId);
}
