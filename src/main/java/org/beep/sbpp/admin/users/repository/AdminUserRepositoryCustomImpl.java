package org.beep.sbpp.admin.users.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.users.entities.QUserEntity;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class AdminUserRepositoryCustomImpl implements AdminUserRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QUserEntity user = QUserEntity.userEntity;

    @Override
    public Page<UserEntity> findAllWithFilterAndSort(Pageable pageable, String category, String keyword, String status, Boolean social) {

        var query = queryFactory.selectFrom(user);

        // 1) 검색 조건 빌드
        BooleanBuilder builder = new BooleanBuilder();

        boolean hasKeyword = keyword != null && !keyword.isBlank();
        String trimmedKeyword = hasKeyword ? keyword.trim() : null;

        if (hasKeyword) {
            switch (category) {
                case "userId":
                    try {
                        Long userId = Long.parseLong(trimmedKeyword);
                        builder.and(user.userId.eq(userId));
                    } catch (NumberFormatException e) {
                        // 숫자가 아니면 무시하고 빈 결과로
                        builder.and(user.userId.eq(-1L));
                    }
                    break;
                case "email":
                    // 문자열이 포함된 이메일에 대하여
                    builder.and(user.email.containsIgnoreCase(trimmedKeyword));
                    break;
                case "all":
                    break;
            }
        }

        // 2) socil 필터
        if (social == true) {
            builder.and(user.isSocial.eq(true));
        }

        // 3) status 필터
        if (status != null) {
            try {
                Status statusEnum = Status.valueOf(status.toUpperCase());
                builder.and(user.status.eq(statusEnum));
            } catch (IllegalArgumentException e) {
                // 잘못된 status 값이면 빈 결과 반환하도록 무조건 false 조건 추가
                builder.and(user.status.eq(Status.ACTIVE).isFalse().and(user.status.eq(Status.DELETED).isFalse()));
            }
        }

        if (builder.hasValue()) {
            query.where(builder);
        }

        // 3) 정렬은 항상 regDate 기준 내림차순
        query.orderBy(user.regDate.desc());

        // 4) 페이징 결과 반환
        List<UserEntity> content = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long totalCount = queryFactory
                .select(user.count())
                .from(user)
                .where(builder)
                .fetchOne();

        long total = totalCount != null ? totalCount : 0L;

        return new PageImpl<>(content, pageable, total);
    }
}
