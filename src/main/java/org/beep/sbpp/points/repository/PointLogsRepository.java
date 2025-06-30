package org.beep.sbpp.points.repository;

import org.beep.sbpp.points.dto.PointLogsDTO;
import org.beep.sbpp.points.entities.PointLogsEntity;
import org.beep.sbpp.points.enums.PointLogsDesc;
import org.beep.sbpp.points.enums.PointLogsType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface PointLogsRepository extends JpaRepository<PointLogsEntity, Long> {

    // 포인트 내역 목록
    @Query("select new org.beep.sbpp.points.dto.PointLogsDTO(p.pointLogId, p.amount, p.type, p.description, p.regDate) " +
            "from PointLogsEntity p " +
            "where p.user.userId = :userId")
    Page<PointLogsDTO> pointLogsList(@Param("userId") Long userId, Pageable pageable);


    // 로그 카운트
    @Query("SELECT COUNT(p) FROM PointLogsEntity p " +
            "WHERE p.user.userId = :userId " +
            "AND p.description = :description " +
            "AND p.type = :type " +
            "AND p.regDate BETWEEN :start AND :end")
    int countReviewEarn(
            @Param("userId") Long userId,
            @Param("description") PointLogsDesc description,
            @Param("type") PointLogsType type,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
