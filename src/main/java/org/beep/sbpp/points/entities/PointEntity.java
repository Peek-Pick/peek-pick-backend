package org.beep.sbpp.points.entities;

import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.reviews.entities.BaseEntity;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.DateTimeException;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_point")
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;

    /*@OneToOne(fetch = FetchType.LAZY)
    private UserEntity userId;*/

    private int amount;


}
