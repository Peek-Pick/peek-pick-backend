package org.beep.sbpp.summary.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.reviews.entities.ReviewEntity;
import org.beep.sbpp.summary.enums.SentimentType;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_review_sentiment")
@Getter
@Setter
public class ReviewSentimentEntity {

    @Id
    @Column(name = "sentiment_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long sentimentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private ReviewEntity reviewEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductBaseEntity productBaseEntity;

    @Enumerated(EnumType.STRING)
    private SentimentType sentiment;

    @Column(columnDefinition = "TEXT")
    private String comment;

    private float score;

    private LocalDateTime analyzedAt;

    private LocalDateTime reviewUpdatedAt;


}
