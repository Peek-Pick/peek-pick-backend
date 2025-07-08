package org.beep.sbpp.reviews.entities;

import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.common.BaseEntity;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.users.entities.UserEntity;

@Getter
@Setter
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductBaseEntity productBaseEntity;

    @Column(name = "comment", nullable = false, columnDefinition = "TEXT")
    private String comment;

    @Column(name = "score", nullable = false)
    private Integer score;

    @Column(name = "recommend_cnt", nullable = false)
    private Integer recommendCnt = 0;

    @Column(name = "report_cnt", nullable = false)
    private Integer reportCnt = 0;

    @Column(name = "is_hidden", nullable = false)
    private Boolean isHidden = false;
}