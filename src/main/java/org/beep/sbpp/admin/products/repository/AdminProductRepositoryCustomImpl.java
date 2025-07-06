package org.beep.sbpp.admin.products.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.products.entities.QProductBaseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminProductRepositoryCustom 의 구현체
 */
@RequiredArgsConstructor
public class AdminProductRepositoryCustomImpl implements AdminProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QProductBaseEntity product = QProductBaseEntity.productBaseEntity;

    @Override
    public Page<ProductBaseEntity> findAllIncludeDeleted(String keyword, Pageable pageable) {
        // 1) 검색 조건 빌드 (soft-delete 필터 없음)
        BooleanBuilder builder = new BooleanBuilder();
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim();
            builder.and(
                    product.name.containsIgnoreCase(kw)
                            .or(product.description.containsIgnoreCase(kw))
            );
        }

        // 2) QueryDSL 쿼리 생성
        var query = queryFactory.selectFrom(product)
                .where(builder);

        // 3) 정렬
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        pageable.getSort().forEach(order -> {
            PathBuilder<ProductBaseEntity> path =
                    new PathBuilder<>(ProductBaseEntity.class, product.getMetadata());
            Order dir = order.isAscending() ? Order.ASC : Order.DESC;
            OrderSpecifier<?> spec;
            if ("score".equals(order.getProperty())) {
                spec = new OrderSpecifier<>(dir, path.getNumber("score", BigDecimal.class))
                        .nullsLast();
            } else if ("likeCount".equals(order.getProperty())) {
                spec = new OrderSpecifier<>(dir, path.getNumber("likeCount", Integer.class));
            } else {
                spec = new OrderSpecifier<>(dir, path.getNumber("productId", Long.class));
            }
            orders.add(spec);
        });
        query.orderBy(orders.toArray(new OrderSpecifier[0]));

        // 4) 페이징 & 카운트
        long total = query.fetchCount();
        List<ProductBaseEntity> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }
}
