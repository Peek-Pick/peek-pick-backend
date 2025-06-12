// src/main/java/org/beep/sbpp/products/repository/ProductTagUserRepositoryImpl.java
package org.beep.sbpp.products.repository;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.products.dto.ProductListDTO;
import org.beep.sbpp.products.entities.QProductEntity;
import org.beep.sbpp.products.entities.QProductTagEntity;
import org.beep.sbpp.tags.entities.QTagUserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductTagUserRepositoryImpl implements ProductTagUserRepository {

    private final JPAQueryFactory queryFactory;
    private final QProductEntity product       = QProductEntity.productEntity;
    private final QProductTagEntity productTag = QProductTagEntity.productTagEntity;
    private final QTagUserEntity tagUser       = QTagUserEntity.tagUserEntity;

    @Override
    public Page<ProductListDTO> findRecommendedByUserId(Long userId, Pageable pageable) {
        // 1) 사용자 관심 태그 ID만 먼저 조회
        List<Long> tagIds = queryFactory
                .select(tagUser.tag.tagId)
                .from(tagUser)
                .where(tagUser.user.userId.eq(userId))
                .fetch();

        if (tagIds.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // 2) 총 건수를 distinct count 로 조회
        long total = queryFactory
                .select(product.productId.countDistinct())
                .from(productTag)
                .join(productTag.productEntity, product)
                .where(
                        product.isDelete.eq(false),
                        productTag.tagEntity.tagId.in(tagIds)
                )
                .fetchOne();

        // 3) 중복 제거(selectDistinct) + Projection으로 필요한 컬럼만 바로 DTO에 매핑
        List<ProductListDTO> content = queryFactory
                .selectDistinct(Projections.constructor(
                        ProductListDTO.class,
                        product.productId,
                        product.barcode,
                        product.name,
                        product.category,
                        product.imgUrl,
                        product.likeCount,
                        product.reviewCount,
                        product.score,
                        Expressions.constant(false),  // isLiked 초기값
                        product.isDelete
                ))
                .from(productTag)
                .join(productTag.productEntity, product)
                .where(
                        product.isDelete.eq(false),
                        productTag.tagEntity.tagId.in(tagIds)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.likeCount.desc())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }
}
