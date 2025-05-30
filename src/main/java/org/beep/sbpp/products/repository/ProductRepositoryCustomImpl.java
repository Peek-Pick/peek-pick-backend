package org.beep.sbpp.products.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.products.entities.ProductEntity;
import org.beep.sbpp.products.entities.QProductEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class ProductRepositoryCustomImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QProductEntity product = QProductEntity.productEntity;

    @Override
    public Page<ProductEntity> findAllWithFilterAndSort(String category, String keyword, Pageable pageable) {
        var query = queryFactory.selectFrom(product);

        // 1) 검색 조건 빌드
        BooleanBuilder builder = new BooleanBuilder();
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim();
            builder.and(
                    product.name.containsIgnoreCase(kw)
                            .or(product.description.containsIgnoreCase(kw))
            );
        }
        if (category != null && !category.isBlank()) {
            String cat = category.trim();
            Predicate categoryPredicate = buildCategoryPredicate(cat);
            if (categoryPredicate != null) {
                builder.and(categoryPredicate);
            }
        }
        if (builder.hasValue()) {
            query.where(builder);
        }

        // 2) 정렬
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        for (org.springframework.data.domain.Sort.Order order : pageable.getSort()) {
            PathBuilder<ProductEntity> path = new PathBuilder<>(ProductEntity.class, product.getMetadata());
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            OrderSpecifier<?> spec;
            if ("score".equals(order.getProperty())) {
                spec = new OrderSpecifier<>(direction, path.getNumber("score", BigDecimal.class)).nullsLast();
            } else if ("likeCount".equals(order.getProperty())) {
                spec = new OrderSpecifier<>(direction, path.getNumber("likeCount", Integer.class));
            } else {
                spec = new OrderSpecifier<>(direction, path.getNumber("productId", Long.class));
            }
            orders.add(spec);
        }
        query.orderBy(orders.toArray(new OrderSpecifier[0]));

        // 3) 페이징 및 결과 반환
        long total = query.fetchCount();
        List<ProductEntity> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 프론트엔드에서 전달된 카테고리 키에 따라 OR 검색할 키워드를 매핑하여 Predicate 반환
     * 매핑되지 않은 키는 기본 containsIgnoreCase 검색으로 fallback
     */
    private Predicate buildCategoryPredicate(String categoryKey) {
        List<String> keywords;
        switch (categoryKey) {
            case "과자류":
                keywords = List.of("과자", "비스켓");
                break;
            case "김밥":
                keywords = List.of("김밥");
                break;
            case "면류":
                keywords = List.of("봉지면", "용기면", "조리면", "면류", "기타면류", "냉동면", "냉장면", "건면");
                break;
            case "빵, 디저트":
                keywords = List.of("디저트", "빵");
                break;
            case "아이스크림":
                keywords = List.of("아이스크림");
                break;
            case "캔디류":
                keywords = List.of("캔디", "껌", "젤리");
                break;
            case "음료":
                keywords = List.of("음료", "커피", "가공유");
                break;
            case "샌드위치-햄버거":
                keywords = List.of("샌드위치", "버거");
                break;
            case "도시락":
                keywords = List.of("도시락");
                break;
            case "안주":
                keywords = List.of("안주");
                break;
            default:
                // 매핑이 없는 경우 기존 키로 contains 검색
                return product.category.containsIgnoreCase(categoryKey);
        }
        // OR 조건으로 묶기
        BooleanBuilder cb = new BooleanBuilder();
        for (String kw : keywords) {
            cb.or(product.category.containsIgnoreCase(kw));
        }
        return cb;
    }
}
