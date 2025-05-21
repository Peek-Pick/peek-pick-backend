package org.beep.sbpp.reviews.entities;

import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.common.BaseEntity;
import org.beep.sbpp.users.entities.UserEntity;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_review")
public class ReviewEntity extends BaseEntity {
    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Column(name = "comment", nullable = false, columnDefinition = "TEXT")
    private String comment;

    // 0이상 5이하 정수로 유효성 검사 필요
    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "recommend_cnt", nullable = false)
    private Integer recommendCnt = 0;

    @Column(name = "is_hidden", nullable = false)
    private Boolean isHidden = false;

    @Column(name = "is_delete", nullable = false)
    private Boolean isDelete = false;
}