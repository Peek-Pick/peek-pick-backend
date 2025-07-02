package org.beep.sbpp.admin.points.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.points.entities.PointStoreEntity;
import org.beep.sbpp.points.entities.QPointStoreEntity;
import org.beep.sbpp.points.enums.PointProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RequiredArgsConstructor
public class AdminPointRepositoryCustomImpl implements AdminPointRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    private final QPointStoreEntity pointStore = QPointStoreEntity.pointStoreEntity;

    @Override
    public Page<PointStoreEntity> findAllWithFilterAndSort(Pageable pageable, String category, String keyword, Boolean hidden) {

        var query = queryFactory.selectFrom(pointStore);

        // 검색 조건을 담을 빌더 생성
        BooleanBuilder builder = new BooleanBuilder();

        // 키워드가 있는지 확인
        boolean hasKeyword = keyword != null && !keyword.isBlank();
        String trimmedKeyword = hasKeyword ? keyword.trim() : null;

        // 카테고리 별 조건
        if (hasKeyword) {
            switch (category) {
                case "pointstoreId":
                    // ID 검색: 숫자로 변환해서 일치하는 항목만 조회
                    try {
                        Long pointstoreId = Long.parseLong(trimmedKeyword);
                        builder.and(pointStore.pointstoreId.eq(pointstoreId));
                    } catch (NumberFormatException e) {
                        // 숫자 아닌 값 들어오면 결과 없도록 -1로 설정
                        builder.and(pointStore.pointstoreId.eq(-1L));
                    }
                    break;

                case "productType":
                    // productType ENUM 타입 필터링
                    try {
                        PointProductType type = PointProductType.valueOf(trimmedKeyword.toUpperCase());
                        builder.and(pointStore.productType.eq(type));
                    } catch (IllegalArgumentException e) {
                        // 존재하지 않는 enum 값이면 결과 없음 처리
                        builder.and(pointStore.pointstoreId.eq(-1L));
                    }
                    break;

                case "item":
                    // 상품 이름에 키워드 포함 여부 (대소문자 무시)
                    builder.and(pointStore.item.containsIgnoreCase(trimmedKeyword));
                    break;

                case "all":
                default:
                    // "all" 또는 잘못된 값 -> 숨김 처리 안 된 상품만 조회
                    builder.and(pointStore.isHidden.eq(false));
                    break;
            }
        }

        // 숨김 필터링
        if (hidden != null) {
            builder.and(pointStore.isHidden.eq(hidden));
        }

        if (builder.hasValue()) {
            query.where(builder);
        }

        // 생성일 내림차순 정렬
        query.orderBy(pointStore.pointstoreId.desc());

        // 페이징 처리: 데이터 fetch
        List<PointStoreEntity> content = query
                .offset(pageable.getOffset())      // 시작 위치
                .limit(pageable.getPageSize())     // 한 페이지 크기
                .fetch();

        // 전체 개수 조회 (builder 조건 동일하게 적용)
        Long total = queryFactory
                .select(pointStore.count())
                .from(pointStore)
                .where(builder)
                .fetchOne();

        // 결과 Page 객체로 반환
        return new PageImpl<>(content, pageable, total != null ? total : 0);
    }

}
