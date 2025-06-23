package org.beep.sbpp.admin.points.service;

import org.beep.sbpp.admin.points.dto.PointStoreAddDTO;
import org.beep.sbpp.admin.points.dto.PointStoreDTO;
import org.beep.sbpp.admin.points.dto.PointStoreListDTO;
import org.beep.sbpp.points.entities.PointStoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminPointService {

    Long add(PointStoreAddDTO dto);

    PointStoreDTO read(Long pointstoreId);

    Page<PointStoreListDTO> list(Pageable pageable, String category, String keyword, Boolean hidden);

    void modify(PointStoreAddDTO dto);

    void delete(Long pointstoreId);


    //DTO -> Entity 변환
    default PointStoreEntity addDTOToEntity(PointStoreAddDTO dto) {
        PointStoreEntity entity = PointStoreEntity.builder()
                .item(dto.getItem())
                .description(dto.getDescription())
                .price(dto.getPrice())
                .productType(dto.getProductType())
                .imgUrl(dto.getImgUrl())
                .build();

        return entity;
    }
}
