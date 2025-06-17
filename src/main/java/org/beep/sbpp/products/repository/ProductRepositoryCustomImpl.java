package org.beep.sbpp.products.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.products.entities.ProductEntity;
import org.beep.sbpp.products.entities.QProductEntity;
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
    private final QProductEntity product = QProductEntity.productEntity;

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
    public List<ProductEntity> findAllWithCursorAndFilter(String category, String keyword, Integer lastValue, Long lastProductId, int size, String sortKey) {
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
        List<String> keywords;
        switch (categoryKey) {
            case "과자류": keywords = List.of("과자", "비스켓"); break;
            case "김밥": keywords = List.of("김밥"); break;
            case "면류": keywords = List.of("봉지면", "용기면", "조리면", "면류", "기타면류", "냉동면", "냉장면", "건면"); break;
            case "빵, 디저트": keywords = List.of("디저트", "빵"); break;
            case "아이스크림": keywords = List.of("아이스크림"); break;
            case "캔디류": keywords = List.of("캔디", "껌", "젤리"); break;
            case "음료": keywords = List.of("음료", "커피", "가공유"); break;
            case "샌드위치-햄버거": keywords = List.of("샌드위치", "버거"); break;
            case "도시락": keywords = List.of("도시락"); break;
            case "안주": keywords = List.of("안주"); break;
            default: return product.category.containsIgnoreCase(categoryKey);
        }

        BooleanBuilder cb = new BooleanBuilder();
        for (String kw : keywords) {
            cb.or(product.category.containsIgnoreCase(kw));
        }
        return cb;
    }
}
