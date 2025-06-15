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
import org.beep.sbpp.products.entities.ProductEntity;
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
    private final FileUploadUtil fileUploadUtil;

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

        // 파일 업로드 처리
        List<String> uploadedFileName = new ArrayList<>();

        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            try {
                uploadedFileName = fileUploadUtil.uploadFiles("points", dto.getImageFile());
            } catch (Exception e) {
                log.error("파일 업로드 실패: " + e.getMessage());
                // 예외 처리
            }
        }

        // DTO에도 저장 (프론트에서 필요하거나 나중에 로그 찍을 때 등등)
        dto.setImgUrl(uploadedFileName.get(0));

        // 업로드된 파일명을 엔티티에 추가 (DB 저장용)
        PointStoreEntity entity = addDTOToEntity(dto);
        entity.changeImg(uploadedFileName.get(0));

        repository.save(entity);

        return entity.getPointstoreId();
    }

    @Override
    public PointStoreDTO read(Long pointstoreId) {

        return new PointStoreDTO(repository.selectOne(pointstoreId));
    }



    @Override
    public void modify(PointStoreAddDTO dto) {

        //상품 엔티티 조회한 후에
        PointStoreEntity pointStoreEntity = repository.selectOne(dto.getPointstoreId());
        //변경 내용을 반영하고
        pointStoreEntity.changePname(dto.getItem());
        pointStoreEntity.changePrice(dto.getPrice());
        pointStoreEntity.changeDesc(dto.getDescription());
        pointStoreEntity.changeType(dto.getProductType());

        // 새 파일이 있으면 기존 이미지 삭제 후 새 이미지 업로드
        if (dto.getImageFile() != null && !dto.getImageFile().isEmpty()) {
            // 기존 이미지 삭제
            if (dto.getImgUrl() != null && !dto.getImgUrl().isEmpty()) {
                fileUploadUtil.deleteFile(dto.getImgUrl());
            }
            try {
                // 파일 업로드
                List<String> uploadedFileNames = fileUploadUtil.uploadFiles("points", dto.getImageFile());
                // 업로드된 파일명을 엔티티에 추가
                if (!uploadedFileNames.isEmpty()) {
                    String fileName = uploadedFileNames.get(0);
                    pointStoreEntity.changeImg(fileName);
                    dto.setImgUrl(fileName); //DTO에도 반영
                }
            } catch (Exception e) {
                log.error("파일 업로드 실패: " + e.getMessage());
            }
        } else {
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
