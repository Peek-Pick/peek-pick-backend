package org.beep.sbpp.points.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.points.dto.PointStoreAddDTO;
import org.beep.sbpp.points.dto.PointStoreDTO;
import org.beep.sbpp.points.dto.PointStoreListDTO;
import org.beep.sbpp.points.entities.PointStoreEntity;
import org.beep.sbpp.points.enums.PointProductType;
import org.beep.sbpp.points.repository.PointStoreRepository;
import org.beep.sbpp.users.enums.CouponStatus;
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
public class PointStoreServiceImpl implements PointStoreService {

    private final PointStoreRepository repository;
    private final FileUploadUtil fileUploadUtil;

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
    public Page<PointStoreListDTO> list(String productType, Pageable pageable) {

        // status가 null이거나 "ALL"이면 전체 조회
        if (productType == null || productType.isBlank() || productType.equalsIgnoreCase("ALL")) {
            return repository.list(pageable);
        }
        // status 조건 필터링
        try {
            PointProductType couponType = PointProductType.valueOf(productType);
            return repository.listByType(couponType, pageable);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid coupon status: {}", productType);
            return Page.empty(pageable);
        }
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
