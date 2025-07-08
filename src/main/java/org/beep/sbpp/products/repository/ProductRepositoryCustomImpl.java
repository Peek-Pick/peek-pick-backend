package org.beep.sbpp.products.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.products.entities.QProductBaseEntity;
import org.beep.sbpp.products.entities.QProductEnEntity;
import org.beep.sbpp.products.entities.QProductJaEntity;
import org.beep.sbpp.products.entities.QProductKoEntity;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 다국어 지원 커서 기반 상품 조회를 위한 QueryDSL 커스텀 구현체
 */
@Slf4j
@RequiredArgsConstructor
@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QProductBaseEntity base = QProductBaseEntity.productBaseEntity;
    private final QProductKoEntity ko     = QProductKoEntity.productKoEntity;
    private final QProductEnEntity en     = QProductEnEntity.productEnEntity;
    private final QProductJaEntity ja     = QProductJaEntity.productJaEntity;

    /**
     * 커서 기반 상품 조회 (랭킹/검색)
     * - keyword: 언어별 name/description 검색
     * - category: 언어별 category 컬럼을 그대로 필터링
     * - soft delete 제외
     * - 커서 조건: likeCount 또는 score 기준
     * - 정렬: sortKey DESC, productId ASC
     *
     * @param category      (언어별) 카테고리 필터, nullable
     * @param keyword       검색어, nullable
     * @param lastValue     정렬 기준 마지막 값
     * @param lastProductId 마지막 상품 ID (보조 커서)
     * @param size          페이지 크기
     * @param sortKey       "likeCount" or "score"
     * @param lang          "ko", "en", "ja"
     * @return BaseEntity 리스트
     */
    @Override
    public List<ProductBaseEntity> findAllWithCursorAndFilter(
            @Nullable String category,
            @Nullable String keyword,
            @Nullable Integer lastValue,
            @Nullable Long lastProductId,
            int size,
            String sortKey,
            String lang
    ) {
        var query = queryFactory.selectFrom(base);
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(base.isDelete.eq(false));

        // 1) 언어별 조인 및 필터 (keyword, category 모두 언어별 Entity의 컬럼 사용)
        switch (lang.toLowerCase().split("[-_]")[0]) {
            case "ko":
                query.join(ko).on(ko.productBase.eq(base));
                if (keyword != null && !keyword.isBlank()) {
                    String kw = keyword.trim();
                    builder.and(
                            ko.name.containsIgnoreCase(kw)
                                    .or(ko.description.containsIgnoreCase(kw))
                    );
                }
                if (category != null && !category.isBlank()) {
                    builder.and(ko.category.eq(category.trim()));  // 언어별 category 필터
                }
                break;

            case "en":
                query.join(en).on(en.productBase.eq(base));
                if (keyword != null && !keyword.isBlank()) {
                    String kw = keyword.trim();
                    builder.and(
                            en.name.containsIgnoreCase(kw)
                                    .or(en.description.containsIgnoreCase(kw))
                    );
                }
                if (category != null && !category.isBlank()) {
                    builder.and(en.category.eq(category.trim()));
                }
                break;

            case "ja":
                query.join(ja).on(ja.productBase.eq(base));
                if (keyword != null && !keyword.isBlank()) {
                    String kw = keyword.trim();
                    builder.and(
                            ja.name.containsIgnoreCase(kw)
                                    .or(ja.description.containsIgnoreCase(kw))
                    );
                }
                if (category != null && !category.isBlank()) {
                    builder.and(ja.category.eq(category.trim()));
                }
                break;

            default:
                log.error("Unsupported language passed to repository: {}", lang);  // ④
                throw new IllegalArgumentException("지원하지 않는 언어: " + lang);
        }

        // 2) 커서 조건 + 정렬 분기
        if ("score".equals(sortKey)) {
            // score 기준 페이징
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
            query.where(builder)
                    .orderBy(base.score.desc().nullsLast(), base.productId.asc())
                    .limit(size);

        } else {
            // likeCount 기준 페이징
            if (lastValue != null && lastProductId != null) {
                builder.and(
                        base.likeCount.lt(lastValue)
                                .or(base.likeCount.eq(lastValue).and(base.productId.gt(lastProductId)))
                );
            }
            query.where(builder)
                    .orderBy(base.likeCount.desc(), base.productId.asc())
                    .limit(size);
        }

        return query.fetch();
    }
}
