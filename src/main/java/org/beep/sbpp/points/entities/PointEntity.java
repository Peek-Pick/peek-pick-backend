package org.beep.sbpp.points.entities;

import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.common.BaseEntity;
import org.beep.sbpp.users.entities.UserEntity;

@Entity
@Table(name = "tbl_point")
@Getter
@ToString(exclude = {"userId"})
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id", nullable = false)
    private Long pointId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @Column(name = "amount")
    private int amount;


    // 포인트 amount 변경
    public void changeAmount(int amount) {
        this.amount = amount;
    }

}
