package org.beep.sbpp.points.entities;

import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.points.enums.PointLogsDesc;
import org.beep.sbpp.points.enums.PointLogsType;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_point_logs")
@EntityListeners(value = AuditingEntityListener.class)
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointLogsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointLogId;

    /*@ManyToOne(fetch = FetchType.LAZY)
    private UserEntity userId;*/

    private int amount;

    private PointLogsType type;

    private PointLogsDesc description;

    @CreatedDate
    @Column(name = "reg_date", updatable = false)
    protected LocalDateTime regDate;


}
