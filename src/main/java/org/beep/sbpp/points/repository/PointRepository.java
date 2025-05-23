package org.beep.sbpp.points.repository;

import org.beep.sbpp.points.entities.PointEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointRepository extends JpaRepository<PointEntity, Long> {

    Optional<PointEntity> findByUser_UserId(Long userId);
}
