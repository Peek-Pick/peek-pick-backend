package org.beep.sbpp.points.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "tbl_point_logs")
@EntityListeners(value = AuditingEntityListener.class) // JPA Auditing 기능 사용 가능하게 함 (예: 생성일, 수정일 자동 관리 등)
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




}
