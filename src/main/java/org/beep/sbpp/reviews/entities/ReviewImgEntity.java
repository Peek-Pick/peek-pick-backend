package org.beep.sbpp.reviews.entities;

import jakarta.persistence.*;
import lombok.*;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_review_img")
public class ReviewImgEntity {
    @Id
    @Column(name = "review_img_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewImgId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private ReviewEntity reviewEntity;

    @Column(name = "img_url", nullable = false)
    private String imgUrl;
}