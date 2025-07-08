package org.beep.sbpp.push.repository;

import org.beep.sbpp.push.entities.PushScheduleEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface PushScheduleRepository extends JpaRepository<PushScheduleEntity, Long> {
    List<PushScheduleEntity> findBySendTimeBefore(LocalDateTime now);
    void deleteByUserId(Long userId);
    List<PushScheduleEntity> findByUserId(Long userId);
}
