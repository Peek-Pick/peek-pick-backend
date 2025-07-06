package org.beep.sbpp.products.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.products.entities.QProductBaseEntity;
import org.beep.sbpp.products.entities.QProductTagEntity;
import org.beep.sbpp.tags.entities.QTagUserEntity;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * 사용자 관심 태그 기반 상품 추천 리포지토리 구현체
 */
@Repository
@RequiredArgsConstructor
public class ProductTagUserRepositoryImpl implements ProductTagUserRepository {

    private final JPAQueryFactory queryFactory;
    private final QProductBaseEntity product       = QProductBaseEntity.productBaseEntity;
    private final QProductTagEntity productTag = QProductTagEntity.productTagEntity;
    private final QTagUserEntity tagUser       = QTagUserEntity.tagUserEntity;

    /**
     * 관심 태그 기반 추천 상품을 커서 기반으로 조회
     * - 정렬 기준: score
     * - 정렬 순서: 주 정렬 DESC, 보조 정렬 productId ASC
     * - 커서 조건: 정렬 기준에 따라 분기
     */
    @Override
    public List<ProductBaseEntity> findRecommendedByUserIdWithCursor(
            Long userId,
            Integer lastValue,
            Long lastProductId,
            int size
    ) {
        List<Long> tagIds = queryFactory
                .select(tagUser.tag.tagId)
                .from(tagUser)
                .where(tagUser.user.userId.eq(userId))
                .fetch();

        if (tagIds.isEmpty()) {
            return Collections.emptyList();
        }

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(product.isDelete.eq(false));
        builder.and(productTag.tagEntity.tagId.in(tagIds));

        if (lastProductId != null) {
            if (lastValue != null) {
                BigDecimal lv = BigDecimal.valueOf(lastValue);
                builder.andAnyOf(
                        product.score.lt(lv),
                        product.score.eq(lv).and(product.productId.gt(lastProductId))
                );
            } else {
                builder.and(product.score.isNull().and(product.productId.gt(lastProductId)));
            }
        }

        return queryFactory
                .select(product)
                .from(productTag)
                .join(productTag.productEntity, product)
                .where(builder)
                .orderBy(
                        product.score.desc().nullsLast(),  // ✅ score 기준 고정
                        product.productId.asc()
                )
                .limit(size)
                .fetch();
    }
}
