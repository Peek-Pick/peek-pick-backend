package org.beep.sbpp.admin.reviews.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.reviews.entities.QReviewEntity;
import org.beep.sbpp.reviews.entities.QReviewReportEntity;
import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class AdminReviewRepositoryCustomImpl implements AdminReviewRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    private final QReviewEntity review = QReviewEntity.reviewEntity;
    private final QReviewReportEntity reviewReport = QReviewReportEntity.reviewReportEntity;

    @Override
    public Page<ReviewEntity> findAllWithFilterAndSort(Pageable pageable, String category, String keyword) {
        var query = queryFactory.selectFrom(review);

        // 1) 검색 조건 빌드
        BooleanBuilder builder = new BooleanBuilder();

        boolean hasKeyword = keyword != null && !keyword.isBlank();
        String trimmedKeyword = hasKeyword ? keyword.trim() : null;

        if (hasKeyword) {
            switch (category) {
                case "reviewId":
                    // 정확히 일치하는 리뷰 id에 대해
                    try {
                        Long reviewId = Long.parseLong(trimmedKeyword);
                        builder.and(review.reviewId.eq(reviewId));
                    } catch (NumberFormatException e) {
                        // 숫자가 아닐 경우 무시하고 빈 결과로
                        builder.and(review.reviewId.eq(-1L));
                    }
                    break;
                case "productId":
                    // 정확히 일치하는 상품 id에 대해
                    try {
                        Long productId = Long.parseLong(trimmedKeyword);
                        builder.and(review.productEntity.productId.eq(productId));
                    } catch (NumberFormatException e) {
                        // 숫자가 아닐 경우 무시하고 빈 결과로
                        builder.and(review.productEntity.productId.eq(-1L));
                    }
                    break;
                case "productName":
                    // 문자열이 포함된 상품 이름에 대해
                    builder.and(review.productEntity.name.containsIgnoreCase(trimmedKeyword));
                    break;
                case "userId":
                    // 정확히 일치하는 유저 id에 대해
                    try {
                        Long userId = Long.parseLong(trimmedKeyword);
                        builder.and(review.userEntity.userId.eq(userId));
                    } catch (NumberFormatException e) {
                        // 숫자가 아닐 경우 무시하고 빈 결과로
                        builder.and(review.userEntity.userId.eq(-1L));
                    }
                    break;
                case "all":
                // "전체" 또는 잘못된 값은 조건 없이 전체 조회
                    break;
            }
        }

        if (builder.hasValue()) {
            query.where(builder);
        }

        // 2) 정렬은 항상 regDate 기준 내림차순
        query.orderBy(review.regDate.desc());

        // 3) 페이징 및 결과 반환
        List<ReviewEntity> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(review.count())
                .from(review)
                .where(builder)
                .fetchOne();

        long total = totalCount != null ? totalCount : 0L;

        return new PageImpl<>(content, pageable, total);
    }
}