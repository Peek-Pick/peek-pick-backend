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
 * 사용자 관심 태그 기반 추천 상품 조회용 리포지토리 구현체
 */
@RequiredArgsConstructor
@Repository
public class ProductTagUserRepositoryImpl implements ProductTagUserRepository {

    private final JPAQueryFactory queryFactory;
    private final QProductBaseEntity base    = QProductBaseEntity.productBaseEntity;
    private final QProductTagEntity pt       = QProductTagEntity.productTagEntity;
    private final QTagUserEntity tagUser     = QTagUserEntity.tagUserEntity;

    /**
     * 관심 태그 기반 추천 상품을 커서 기반으로 조회
     * - 정렬 기준: score (고정)
     * - 주 정렬 DESC, 보조 정렬 productId ASC
     * - 커서 조건: score 기준 분기
     *
     * @param userId        사용자 ID
     * @param lastValue     마지막 score 값 (커서)
     * @param lastProductId 마지막 상품 ID (보조 커서)
     * @param size          페이지 크기
     * @return BaseEntity 리스트
     */
    @Override
    public List<ProductBaseEntity> findRecommendedByUserIdWithCursor(
            Long userId,
            Integer lastValue,
            Long lastProductId,
            int size
    ) {
        // 1) 사용자 관심 태그 ID 조회
        List<Long> tagIds = queryFactory
                .select(tagUser.tag.tagId)
                .from(tagUser)
                .where(tagUser.user.userId.eq(userId))
                .fetch();
        if (tagIds.isEmpty()) {
            return Collections.emptyList();
        }

        // 2) 기본 필터: soft delete & 관심 태그 매칭
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(base.isDelete.eq(false))
                .and(pt.tagEntity.tagId.in(tagIds));

        // 3) 커서(Score) 페이징
        if (lastProductId != null) {
            if (lastValue != null) {
                BigDecimal lv = BigDecimal.valueOf(lastValue);
                builder.andAnyOf(
                        base.score.lt(lv),
                        base.score.eq(lv).and(base.productId.gt(lastProductId))
                );
            } else {
                builder.and(base.score.isNull().and(base.productId.gt(lastProductId)));
            }
        }

        // 4) 조인 → where → 정렬 → limit
        return queryFactory
                .select(base)
                .from(pt)
                .join(pt.productBaseEntity, base)
                .where(builder)
                .orderBy(base.score.desc().nullsLast(), base.productId.asc())
                .limit(size)
                .fetch();
    }
}
