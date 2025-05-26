package org.beep.sbpp.points.repository;

import org.beep.sbpp.points.dto.PointStoreListDTO;
import org.beep.sbpp.points.entities.PointStoreEntity;
import org.beep.sbpp.points.enums.PointProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PointStoreRepository extends JpaRepository<PointStoreEntity, Long> {

    //조회 - 1개
    @Query("select p from PointStoreEntity p where p.pointstoreId = :pointstoreId ")
    PointStoreEntity selectOne(@Param("pointstoreId") Long pointstoreId);

    //목록
    @Query("select new org.beep.sbpp.points.dto.PointStoreListDTO(p.pointstoreId, p.item, p.price, p.productType, p.imgUrl) " +
            "from PointStoreEntity p " +
            "where p.isHidden = false")
    Page<PointStoreListDTO> list(Pageable pageable);

    //목록 - 상태 필터링
    @Query("select new org.beep.sbpp.points.dto.PointStoreListDTO(p.pointstoreId, p.item, p.price, p.productType, p.imgUrl) " +
            "from PointStoreEntity p " +
            "where p.isHidden = false and p.productType = :productType")
    Page<PointStoreListDTO> listByType(@Param("productType") PointProductType productType, Pageable pageable);
}
