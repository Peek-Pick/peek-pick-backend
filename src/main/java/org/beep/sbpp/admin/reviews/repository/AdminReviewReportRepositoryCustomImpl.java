package org.beep.sbpp.admin.reviews.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.reviews.entities.QReviewReportEntity;
import org.beep.sbpp.reviews.entities.ReviewReportEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class AdminReviewReportRepositoryCustomImpl implements AdminReviewReportRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    private final QReviewReportEntity reviewReport = QReviewReportEntity.reviewReportEntity;

    @Override
    public Page<ReviewReportEntity> findAllWithFilterAndSort(Pageable pageable, String category, String keyword, Boolean hidden) {
        var query = queryFactory.selectFrom(reviewReport);

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
                        builder.and(reviewReport.reviewEntity.reviewId.eq(reviewId));
                    } catch (NumberFormatException e) {
                        // 숫자가 아닐 경우 무시하고 빈 결과로
                        builder.and(reviewReport.reviewEntity.reviewId.eq(-1L));
                    }
                    break;
                case "reviewerId":
                    // 정확히 일치하는 작성자 id에 대해
                    try {
                        Long reviewerId = Long.parseLong(trimmedKeyword);
                        builder.and(reviewReport.reviewEntity.userEntity.userId.eq(reviewerId));
                    } catch (NumberFormatException e) {
                        // 숫자가 아닐 경우 무시하고 빈 결과로
                        builder.and(reviewReport.reviewEntity.userEntity.userId.eq(-1L));
                    }
                    break;
                case "userId":
                    // 정확히 일치하는 유저 id에 대해
                    try {
                        Long userId = Long.parseLong(trimmedKeyword);
                        builder.and(reviewReport.userEntity.userId.eq(userId));
                    } catch (NumberFormatException e) {
                        // 숫자가 아닐 경우 무시하고 빈 결과로
                        builder.and(reviewReport.userEntity.userId.eq(-1L));
                    }
                    break;
                case "all":
                    // "전체" 또는 잘못된 값은 조건 없이 전체 조회
                    break;
            }
        }

        // 2) hidden 필터 조건 추가
        if (hidden == true) {
            builder.and(reviewReport.reviewEntity.isHidden.eq(true));
        }

        if (builder.hasValue()) {
            query.where(builder);
        }

        // 3) 정렬은 항상 regDate 기준 내림차순
        query.orderBy(reviewReport.regDate.desc());

        // 4) 페이징 및 결과 반환
        List<ReviewReportEntity> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(reviewReport.count())
                .from(reviewReport)
                .where(builder)
                .fetchOne();

        long total = totalCount != null ? totalCount : 0L;

        return new PageImpl<>(content, pageable, total);
    }
}