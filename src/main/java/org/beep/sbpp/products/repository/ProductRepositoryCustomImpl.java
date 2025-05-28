package org.beep.sbpp.products.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
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

        // 1) BooleanBuilder로 name/description 검색 AND category 필터 동시 적용
        BooleanBuilder builder = new BooleanBuilder();
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim();
            builder.and(
                    product.name.containsIgnoreCase(kw)
                            .or(product.description.containsIgnoreCase(kw))
            );
        }
        if (category != null && !category.isBlank()) {
            builder.and(
                    product.category.containsIgnoreCase(category.trim())
            );
        }
        if (builder.hasValue()) {
            query.where(builder);
        }

        // 2) 정렬: score, likeCount, 그 외는 productId 기준
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        for (org.springframework.data.domain.Sort.Order order : pageable.getSort()) {
            PathBuilder<ProductEntity> path =
                    new PathBuilder<>(ProductEntity.class, product.getMetadata());
            Order direction = order.isAscending() ? Order.ASC : Order.DESC;
            OrderSpecifier<?> spec;

            if ("score".equals(order.getProperty())) {
                spec = new OrderSpecifier<>(
                        direction,
                        path.getNumber("score", BigDecimal.class)
                ).nullsLast();

            } else if ("likeCount".equals(order.getProperty())) {
                spec = new OrderSpecifier<>(
                        direction,
                        path.getNumber("likeCount", Integer.class)
                );

            } else {
                spec = new OrderSpecifier<>(
                        direction,
                        path.getNumber("productId", Long.class)
                );
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
}
