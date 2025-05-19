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
    private Long reviewImgId;

    @OneToOne
    @MapsId  // reviewImgId가 ReviewEntity의 PK를 공유함
    @JoinColumn(name = "tbl_review")
    private ReviewEntity reviewEntity;

    @Column(name = "img_url", nullable = false)
    private String imgUrl;
}