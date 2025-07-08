// src/main/java/org/beep/sbpp/admin/reviews/repository/AdminReviewRepositoryCustomImpl.java
package org.beep.sbpp.admin.reviews.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.products.entities.QProductBaseEntity;
import org.beep.sbpp.products.entities.QProductEnEntity;
import org.beep.sbpp.products.entities.QProductJaEntity;
import org.beep.sbpp.products.entities.QProductKoEntity;
import org.beep.sbpp.reviews.entities.QReviewEntity;
import org.beep.sbpp.reviews.entities.QReviewReportEntity;
import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * AdminReviewRepositoryCustom 구현체
 */
@RequiredArgsConstructor
public class AdminReviewRepositoryCustomImpl implements AdminReviewRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QReviewEntity        review       = QReviewEntity.reviewEntity;
    private final QReviewReportEntity  reviewReport = QReviewReportEntity.reviewReportEntity;

    private final QProductBaseEntity   base         = QProductBaseEntity.productBaseEntity;
    private final QProductKoEntity     ko           = QProductKoEntity.productKoEntity;
    private final QProductEnEntity     en           = QProductEnEntity.productEnEntity;
    private final QProductJaEntity     ja           = QProductJaEntity.productJaEntity;

    @Override
    public Page<ReviewEntity> findAllWithFilterAndSort(
            Pageable pageable,
            String category,
            String keyword,
            Boolean hidden,
            String lang
    ) {
        // 1) 메인 쿼리 준비 (ReviewEntity 기준)
        JPAQuery<ReviewEntity> query = queryFactory.selectFrom(review);

        // 2) join ProductBaseEntity once for potential name-filter
        query.leftJoin(review.productBaseEntity, base);

        // 3) 빌더로 where 조건 누적
        BooleanBuilder builder = new BooleanBuilder();

        // 3-1) keyword 필터
        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim();
            switch (category) {
                case "reviewId":
                    try {
                        Long id = Long.parseLong(kw);
                        builder.and(review.reviewId.eq(id));
                    } catch (NumberFormatException e) {
                        builder.and(review.reviewId.isNull()); // 무조건 빈 결과
                    }
                    break;
                case "productId":
                    try {
                        Long pid = Long.parseLong(kw);
                        builder.and(review.productBaseEntity.productId.eq(pid));
                    } catch (NumberFormatException e) {
                        builder.and(review.productBaseEntity.productId.isNull());
                    }
                    break;
                case "productName":
                    // 언어별 테이블에서 name 컬럼 검색
                    switch (lang.toLowerCase().split("[-_]")[0]) {
                        case "ko":
                            query.leftJoin(ko).on(ko.productBase.eq(base));
                            builder.and(ko.name.containsIgnoreCase(kw));
                            break;
                        case "en":
                            query.leftJoin(en).on(en.productBase.eq(base));
                            builder.and(en.name.containsIgnoreCase(kw));
                            break;
                        case "ja":
                            query.leftJoin(ja).on(ja.productBase.eq(base));
                            builder.and(ja.name.containsIgnoreCase(kw));
                            break;
                        default:
                            throw new IllegalArgumentException("지원하지 않는 언어: " + lang);
                    }
                    break;
                case "userId":
                    try {
                        Long uid = Long.parseLong(kw);
                        builder.and(review.userEntity.userId.eq(uid));
                    } catch (NumberFormatException e) {
                        builder.and(review.userEntity.userId.isNull());
                    }
                    break;
                case "all":
                default:
                    // 아무 조건 추가 없음
            }
        }

        // 3-2 hidden 필터
        if (hidden != null) {
            builder.and(review.isHidden.eq(hidden));
        }

        query.where(builder);

        // 4) 정렬: always regDate desc
        query.orderBy(review.regDate.desc());

        // 5) 페이징
        long total = query.fetchCount();
        List<ReviewEntity> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        return new PageImpl<>(content, pageable, total);
    }
}
