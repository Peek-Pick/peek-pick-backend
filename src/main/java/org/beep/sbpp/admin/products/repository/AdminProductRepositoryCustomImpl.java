// src/main/java/org/beep/sbpp/admin/products/repository/AdminProductRepositoryCustomImpl.java

package org.beep.sbpp.admin.products.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.products.entities.QProductBaseEntity;
import org.beep.sbpp.products.entities.QProductEnEntity;
import org.beep.sbpp.products.entities.QProductJaEntity;
import org.beep.sbpp.products.entities.QProductKoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * AdminProductRepositoryCustom 의 구현체
 */
// src/main/java/org/beep/sbpp/admin/products/repository/AdminProductRepositoryCustomImpl.java
@RequiredArgsConstructor
public class AdminProductRepositoryCustomImpl implements AdminProductRepositoryCustom {
    private final JPAQueryFactory queryFactory;
    private final QProductBaseEntity base = QProductBaseEntity.productBaseEntity;
    private final QProductKoEntity   ko   = QProductKoEntity.productKoEntity;
    private final QProductEnEntity   en   = QProductEnEntity.productEnEntity;
    private final QProductJaEntity   ja   = QProductJaEntity.productJaEntity;

    @Override
    public Page<ProductBaseEntity> findAllIncludeDeleted(
            String keyword, String lang, Pageable pageable
    ) {
        // 1) BooleanBuilder 조건
        BooleanBuilder builder = new BooleanBuilder();
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim();
            switch(lang.toLowerCase().split("[-_]")[0]) {
                case "ko":
                    builder.and(
                            ko.productBase.eq(base)
                                    .and(ko.name.containsIgnoreCase(kw)
                                            .or(ko.description.containsIgnoreCase(kw)))
                    );
                    break;
                case "en":
                    builder.and(
                            en.productBase.eq(base)
                                    .and(en.name.containsIgnoreCase(kw)
                                            .or(en.description.containsIgnoreCase(kw)))
                    );
                    break;
                case "ja":
                    builder.and(
                            ja.productBase.eq(base)
                                    .and(ja.name.containsIgnoreCase(kw)
                                            .or(ja.description.containsIgnoreCase(kw)))
                    );
                    break;
                default:
                    throw new IllegalArgumentException("지원하지 않는 언어: " + lang);
            }
        }

        // 2) base 를 기준으로 쿼리 생성
        var query = queryFactory
                .selectFrom(base)
                .where(builder);

        // 3) 정렬
        List<OrderSpecifier<?>> orders = new ArrayList<>();
        pageable.getSort().forEach(order -> {
            PathBuilder<ProductBaseEntity> path =
                    new PathBuilder<>(ProductBaseEntity.class, base.getMetadata());
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
