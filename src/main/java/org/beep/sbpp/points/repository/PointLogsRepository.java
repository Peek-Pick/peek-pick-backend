package org.beep.sbpp.points.repository;

import org.beep.sbpp.points.entities.PointLogsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PointLogsRepository extends JpaRepository<PointLogsEntity, Long> {
}
