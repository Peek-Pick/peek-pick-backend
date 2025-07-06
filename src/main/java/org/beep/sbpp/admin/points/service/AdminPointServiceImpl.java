package org.beep.sbpp.admin.points.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.admin.points.dto.PointStoreAddDTO;
import org.beep.sbpp.admin.points.dto.PointStoreDTO;
import org.beep.sbpp.admin.points.dto.PointStoreListDTO;
import org.beep.sbpp.admin.reviews.dto.AdminReviewSimpleDTO;
import org.beep.sbpp.points.entities.PointStoreEntity;
import org.beep.sbpp.points.enums.PointProductType;
import org.beep.sbpp.admin.points.repository.AdminPointRepository;
import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.beep.sbpp.users.entities.UserProfileEntity;
import org.beep.sbpp.util.FileUploadUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminPointServiceImpl implements AdminPointService {

    private final AdminPointRepository repository;

    //어드민 리스트
    @Override
    public Page<PointStoreListDTO> list(Pageable pageable, String category, String keyword, Boolean hidden){
        // pageable - regDate 기준 최신순 정렬, category, keyword - 필터링 기준
        Page<PointStoreEntity> page = repository.findAllWithFilterAndSort(pageable, category, keyword, hidden);

        // Entity → DTO 매핑
        return page.map(product -> PointStoreListDTO.builder()
                .pointstoreId(product.getPointstoreId())       // ID
                .item(product.getItem())                       // 상품명
                .price(product.getPrice())                     // 가격
                .productType(product.getProductType())         // 상품 타입
                .imgUrl(product.getImgUrl())                   // 이미지 경로
                .build()
        );
    }

    @Override
    public Long add(PointStoreAddDTO dto) {

        // 프론트에서 이미 업로드된 이미지 URL을 받아서 처리함

        PointStoreEntity entity = addDTOToEntity(dto);
        entity.changeImg(dto.getImgUrl());

        repository.save(entity);

        return entity.getPointstoreId();
    }

    @Override
    public PointStoreDTO read(Long pointstoreId) {

        return new PointStoreDTO(repository.selectOne(pointstoreId));
    }

    @Override
    public void modify(PointStoreAddDTO dto) {

        // 기존 엔티티 조회
        PointStoreEntity pointStoreEntity = repository.selectOne(dto.getPointstoreId());

        // 변경 내용 반영
        pointStoreEntity.changePname(dto.getItem());
        pointStoreEntity.changePrice(dto.getPrice());
        pointStoreEntity.changeDesc(dto.getDescription());
        pointStoreEntity.changeType(dto.getProductType());

        // 이미지 URL 업데이트
        if (dto.getImgUrl() != null && !dto.getImgUrl().isEmpty()) {
            pointStoreEntity.changeImg(dto.getImgUrl());
        }

        repository.save(pointStoreEntity);
    }

    @Override
    public void delete(Long pointstoreId) {
        PointStoreEntity pointStoreEntity = repository.selectOne(pointstoreId);

        pointStoreEntity.softDelete();

        repository.save(pointStoreEntity);
    }
}
