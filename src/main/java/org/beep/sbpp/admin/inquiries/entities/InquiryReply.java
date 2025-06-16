package org.beep.sbpp.admin.inquiries.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.common.BaseEntity;
import org.beep.sbpp.inquiries.entities.Inquiry;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_inquiry_reply")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryReply extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long replyId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", nullable = false, unique = true)
    private Inquiry inquiry;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @PrePersist
    protected void onCreate() {
        this.regDate = LocalDateTime.now();
        this.modDate = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.modDate = LocalDateTime.now();
    }
}