package org.beep.sbpp.barcode.repository;

import org.beep.sbpp.barcode.entities.BarcodeHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface BarcodeHistoryRepository extends JpaRepository<BarcodeHistoryEntity, Long> {

    @Query("""
    SELECT bh
    FROM BarcodeHistoryEntity bh
    WHERE bh.viewId IN (
        SELECT MAX(bh2.viewId)
        FROM BarcodeHistoryEntity bh2
        WHERE bh2.userId = :userId AND bh2.isBarcodeHistory = true
        GROUP BY bh2.productId
    )
    ORDER BY bh.regDate DESC
""")
    List<BarcodeHistoryEntity> findRecentDistinctByUser(@Param("userId") Long userId);

    @Query("""
    SELECT bh
    FROM BarcodeHistoryEntity bh
    WHERE bh.userId = :userId
      AND bh.productId = :productId
      AND bh.isBarcodeHistory = true
    ORDER BY bh.regDate DESC
""")
    List<BarcodeHistoryEntity> findLatestBarcodeHistoryByUserAndProduct(
            @Param("userId") Long userId,
            @Param("productId") Long productId,
            org.springframework.data.domain.Pageable pageable
    );

    @Modifying
    @Transactional
    @Query("""
    UPDATE BarcodeHistoryEntity bh
    SET bh.isReview = :isReview
    WHERE bh.viewId = (
        SELECT bh2.viewId FROM BarcodeHistoryEntity bh2
        WHERE bh2.userId = :userId
          AND bh2.productId = :productId
          AND bh2.isBarcodeHistory = true
        ORDER BY bh2.regDate DESC
        LIMIT 1
    )
""")
    int updateIsReviewForLatestBarcodeHistory(
            @Param("userId") Long userId,
            @Param("productId") Long productId,
            @Param("isReview") Boolean isReview
    );

}
