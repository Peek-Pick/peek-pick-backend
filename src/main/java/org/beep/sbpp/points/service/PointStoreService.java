package org.beep.sbpp.points.service;

import org.beep.sbpp.points.dto.PointStoreDTO;
import org.beep.sbpp.points.dto.PointStoreListDTO;
import org.beep.sbpp.points.entities.PointStoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PointStoreService {

    Long add(PointStoreDTO pointStoreDTO);

    PointStoreDTO read(Long pointstoreId);

    Page<PointStoreListDTO> list(Pageable pageable);

    void modify(PointStoreDTO pointStoreDTO);

    void delete(Long pointstoreId);


    //DTO -> Entity 변환
    default PointStoreEntity addDTOToEntity(PointStoreDTO dto) {
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
