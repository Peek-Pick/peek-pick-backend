package org.beep.sbpp.points.repository;

import org.beep.sbpp.admin.points.dto.PointStoreListDTO;
import org.beep.sbpp.points.entities.PointEntity;
import org.beep.sbpp.points.enums.PointProductType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PointRepository extends JpaRepository<PointEntity, Long> {

    Optional<PointEntity> findByUser_UserId(Long userId);

    //유저 포인트양
    @Query("select p.amount " +
            "from PointEntity p " +
            "where p.user.userId = :userId")
    Integer getUserPointAmount(@Param("userId") Long userId);

}
