package org.beep.sbpp.reviews.entities;

import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.tags.entities.TagEntity;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_review_tag")
public class ReviewTagEntity {
    @Id
    @Column(name = "review_tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewTagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private ReviewEntity reviewEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private TagEntity tagEntity;
}