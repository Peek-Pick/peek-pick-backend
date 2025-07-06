package org.beep.sbpp.products.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.products.entities.QProductBaseEntity;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 상품 목록 조회를 위한 QueryDSL 커스텀 구현체
 */
@RequiredArgsConstructor
@Repository
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QProductBaseEntity product = QProductBaseEntity.productBaseEntity;

    /**
     * 커서 기반 상품 조회 (랭킹/검색)
     * - category / keyword 조건 적용
     * - sortKey 에 따라 likeCount 또는 score 기준 정렬
     * - 커서 조건도 sortKey 에 따라 분기
     * - 항상 보조 정렬 기준으로 productId ASC
     *
     * @param category        카테고리 필터 (nullable)
     * @param keyword         검색어 (nullable)
     * @param lastValue       정렬 기준 값 (likeCount or score의 마지막 값)
     * @param lastProductId   마지막 상품 ID (보조 커서 기준)
     * @param size            페이지 크기
     * @param sortKey         정렬 기준 ("likeCount" or "score")
     * @return 상품 목록
     */
    @Override
    public List<ProductBaseEntity> findAllWithCursorAndFilter(String category, String keyword, Integer lastValue, Long lastProductId, int size, String sortKey) {
        var query = queryFactory.selectFrom(product);

        // 1) 기본 조건
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(product.isDelete.eq(false));

        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim();
            builder.and(product.name.containsIgnoreCase(kw).or(product.description.containsIgnoreCase(kw)));
        }

        if (category != null && !category.isBlank()) {
            Predicate categoryPredicate = buildCategoryPredicate(category.trim());
            if (categoryPredicate != null) {
                builder.and(categoryPredicate);
            }
        }

        // 2) 커서 조건 + 정렬 분기
        if ("score".equals(sortKey)) {
            if (lastProductId != null) {
                if (lastValue != null) {
                    BigDecimal lv = BigDecimal.valueOf(lastValue);
                    builder.andAnyOf(
                            // ⬇ 수치 비교 (score 있는 그룹)
                            product.score.lt(lv),
                            product.score.eq(lv).and(product.productId.gt(lastProductId))
                    );
                } else {
                    // ⬇ null 그룹 페이징: productId 기준 보조 커서
                    builder.and(product.score.isNull().and(product.productId.gt(lastProductId)));
                }
            }

            query.orderBy(
                    product.score.desc().nullsLast(),
                    product.productId.asc()
            );
        } else {
            if (lastValue != null && lastProductId != null) {
                builder.and(
                        product.likeCount.lt(lastValue)
                                .or(product.likeCount.eq(lastValue)
                                        .and(product.productId.gt(lastProductId)))
                );
            }

            query.orderBy(
                    product.likeCount.desc(),
                    product.productId.asc()
            );
        }

        query.where(builder);
        return query.limit(size).fetch();
    }

    /**
     * 프론트엔드에서 전달된 카테고리 키에 따라 OR 검색할 키워드를 매핑하여 Predicate 반환
     * 매핑되지 않은 키는 기본 containsIgnoreCase 검색으로 fallback
     */
    private Predicate buildCategoryPredicate(String categoryKey) {
        String dbCategory = switch (categoryKey) {
            case "과자류" -> "과자류";
            case "캔디/껌" -> "캔디/껌";
            case "아이스크림" -> "아이스크림";
            case "빵/디저트" -> "빵/디저트";
            case "도시락" -> "도시락";
            case "삼각김밥/김밥" -> "삼각김밥/김밥";
            case "면류" -> "면류";
            case "샌드위치/햄버거" -> "샌드위치/햄버거";
            case "음료" -> "음료";
            case "과일/샐러드" -> "과일/샐러드";
            case "즉석섭취식품" -> "즉석섭취식품";
            case "즉석조리식품" -> "즉석조리식품";
            case "식재료" -> "식재료";
            case "건강식품" -> "건강식품";
            default -> categoryKey; // fallback: 혹시라도 프론트가 새로운 값 넣을 때 대비
        };

        return product.category.eq(dbCategory);
    }
}