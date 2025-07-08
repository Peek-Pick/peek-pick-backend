package org.beep.sbpp.admin.users.repository;

import org.beep.sbpp.users.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AdminUserRepository extends JpaRepository<UserEntity, Long>, AdminUserRepositoryCustom{
    @Query("""
        SELECT FUNCTION('TO_CHAR', u.regDate, 'MM'), COUNT(u.userId)
        FROM UserEntity u
        WHERE u.regDate BETWEEN :startDateTime AND :endDateTime
        GROUP BY FUNCTION('TO_CHAR', u.regDate, 'MM')
        ORDER BY FUNCTION('TO_CHAR', u.regDate, 'MM')
    """)
    List<Object[]> countMonthlyJoinUsers(@Param("startDateTime") LocalDateTime startDateTime,
                                         @Param("endDateTime") LocalDateTime endDateTime);

    @Query("SELECT up.nationality, COUNT(up) FROM UserProfileEntity up GROUP BY up.nationality")
    List<Object[]> countUsersByNationality();

    @Query("SELECT COUNT(u) FROM UserEntity u WHERE EXTRACT(MONTH FROM u.regDate) = :month AND EXTRACT(YEAR FROM u.regDate) = :year")
    Long countByMonth(@Param("month") int month, @Param("year") int year);
}