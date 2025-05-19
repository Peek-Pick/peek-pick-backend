package org.beep.sbpp.points.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.points.dto.PointStoreDTO;
import org.beep.sbpp.points.entities.PointStoreEntity;
import org.beep.sbpp.points.repository.PointStoreRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PointStoreServiceImpl implements PointStoreService {

    private final PointStoreRepository repository;

    @Override
    public Long add(PointStoreDTO pointStoreDTO) {

        PointStoreEntity pointStoreEntity = addDTOToEntity(pointStoreDTO);

        repository.save(pointStoreEntity);

        return pointStoreEntity.getPointstoreId();
    }

    @Override
    public PointStoreDTO read(Long pointstoreId) {

        return new PointStoreDTO(repository.selectOne(pointstoreId));
    }

    @Override
    public Page<PointStoreDTO> list(Pageable pageable) {

        return repository.list(pageable)
                .map(arr -> PointStoreDTO.builder()
                        .pointstoreId((Long) arr[0])
                        .item((String) arr[1])
                        .price((Integer) arr[2])
                        .imgUrl((String) arr[3])
                        .build());

    }

    @Override
    public void modify(PointStoreDTO pointStoreDTO) {

        //상품 엔티티 조회한 후에
        PointStoreEntity pointStoreEntity = repository.selectOne(pointStoreDTO.getPointstoreId());
        //변경 내용을 반영하고
        pointStoreEntity.changePname(pointStoreDTO.getItem());
        pointStoreEntity.changePrice(pointStoreDTO.getPrice());
        pointStoreEntity.changeDesc(pointStoreDTO.getDescription());
        pointStoreEntity.changeType(pointStoreDTO.getProductType());
        pointStoreEntity.changeImg(pointStoreDTO.getImgUrl());

        repository.save(pointStoreEntity);
    }

    @Override
    public void delete(Long pointstoreId) {
        PointStoreEntity pointStoreEntity = repository.selectOne(pointstoreId);

        pointStoreEntity.softDelete();

        repository.save(pointStoreEntity);
    }
}
