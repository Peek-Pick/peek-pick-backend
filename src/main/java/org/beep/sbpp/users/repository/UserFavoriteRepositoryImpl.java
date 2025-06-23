package org.beep.sbpp.users.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.products.entities.ProductLikeEntity;
import org.beep.sbpp.products.entities.QProductEntity;
import org.beep.sbpp.products.entities.QProductLikeEntity;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 커서 기반으로 찜한 상품 조회를 구현
 */
@RequiredArgsConstructor
public class UserFavoriteRepositoryImpl implements UserFavoriteRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QProductLikeEntity like = QProductLikeEntity.productLikeEntity;
    private final QProductEntity product = QProductEntity.productEntity;

    /**
     * 커서 기반 페이징
     * - userId와 isDelete 조건 적용
     * - modDate < ? OR (modDate = ? AND productId < ?) 기준 커서 페이징
     * - 정렬: modDate DESC, productId DESC
     *
     * @param userId         사용자 ID
     * @param lastModDate    마지막 수정일시
     * @param lastProductId  마지막 상품 ID
     * @param size           조회할 개수
     * @return 찜한 상품 목록
     */
    @Override
    public List<ProductLikeEntity> findAllByCursor(Long userId, LocalDateTime lastModDate, Long lastProductId, int size) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(like.userEntity.userId.eq(userId));
        builder.and(like.isDelete.isFalse());

        // 커서 조건 추가
        if (lastModDate != null && lastProductId != null) {
            builder.and(
                    like.modDate.lt(lastModDate)
                            .or(
                                    like.modDate.eq(lastModDate)
                                            .and(like.productEntity.productId.lt(lastProductId))
                            )
            );
        }

        return queryFactory
                .selectFrom(like)
                .join(like.productEntity, product).fetchJoin()
                .where(builder)
                .orderBy(
                        like.modDate.desc(),
                        like.productEntity.productId.desc()
                )
                .limit(size)
                .fetch();
    }
}
