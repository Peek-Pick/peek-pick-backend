package org.beep.sbpp.points.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.DateTimeException;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_point")
@EntityListeners(value = AuditingEntityListener.class) // JPA Auditing 기능 사용 가능하게 함 (예: 생성일, 수정일 자동 관리 등)
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;

    /*@OneToOne(fetch = FetchType.LAZY)
    private UserEntity userId;*/

    private int amount;

    @CreatedDate
    @Column(name = "reg_date", updatable = false)
    protected LocalDateTime regDate;

    @LastModifiedDate
    @Column(name ="mod_date")
    protected LocalDateTime modDate;



}
