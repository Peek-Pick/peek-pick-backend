package org.beep.sbpp.points.entities;

import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.points.enums.PointLogsDesc;
import org.beep.sbpp.points.enums.PointLogsType;
import org.beep.sbpp.users.entities.UserEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_point_logs")
@EntityListeners(value = AuditingEntityListener.class)
@Getter
@ToString(exclude = {"userId"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointLogsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_log_id", nullable = false)
    private Long pointLogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "amount", nullable = false)
    private int amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PointLogsType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "description")
    private PointLogsDesc description;

    @CreatedDate
    @Column(name = "reg_date", updatable = false, nullable = false)
    protected LocalDateTime regDate;


}
