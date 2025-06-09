package org.beep.sbpp.products.repository;

import org.beep.sbpp.products.entities.ProductLikeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ProductLikeRepository extends JpaRepository<ProductLikeEntity, Long> {

    Optional<ProductLikeEntity> findByProductEntity_ProductIdAndUserEntity_UserId(
            Long productId, Long userId);

    @Query("""
        SELECT CASE WHEN COUNT(pl) > 0 THEN true ELSE false END
        FROM ProductLikeEntity pl
        WHERE pl.productEntity.productId = :productId
          AND pl.userEntity.userId = :userId
          AND pl.isDelete = false
    """)
    boolean hasUserLikedProduct(
            @Param("productId") Long productId,
            @Param("userId") Long userId
    );

    /**
     * 좋아요(찜) 재활성화 시, isDelete = false로 만들고 modDate를 현재 시각으로 갱신
     */
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE ProductLikeEntity pl
        SET pl.isDelete  = false,
            pl.modDate   = CURRENT_TIMESTAMP
        WHERE pl.productEntity.productId = :productId
          AND pl.userEntity.userId       = :userId
    """)
    int activateLike(
            @Param("productId") Long productId,
            @Param("userId") Long userId
    );

    /**
     * 좋아요(찜) 비활성화 시, isDelete = true로 만들고 modDate를 현재 시각으로 갱신
     */
    @Modifying(clearAutomatically = true)
    @Query("""
        UPDATE ProductLikeEntity pl
        SET pl.isDelete  = true,
            pl.modDate   = CURRENT_TIMESTAMP
        WHERE pl.productEntity.productId = :productId
          AND pl.userEntity.userId       = :userId
    """)
    int deactivateLike(
            @Param("productId") Long productId,
            @Param("userId") Long userId
    );

    @Modifying
    @Query("""
        UPDATE ProductEntity p
        SET p.likeCount = p.likeCount + 1
        WHERE p.productId = :productId
    """)
    int increaseLikeCount(@Param("productId") Long productId);

    @Modifying
    @Query("""
        UPDATE ProductEntity p
        SET p.likeCount = p.likeCount - 1
        WHERE p.productId = :productId
    """)
    int decreaseLikeCount(@Param("productId") Long productId);
}
