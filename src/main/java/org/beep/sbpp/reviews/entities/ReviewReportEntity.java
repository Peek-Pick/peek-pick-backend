package org.beep.sbpp.reviews.entities;

import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.common.BaseEntity2;
import org.beep.sbpp.reviews.enums.ReportReason;
import org.beep.sbpp.users.entities.UserEntity;

@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_review_report")
public class ReviewReportEntity extends BaseEntity2 {
    @Id
    @Column(name = "review_report_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reviewReportId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private ReviewEntity reviewEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "reason", nullable = false)
    private ReportReason reason;
}