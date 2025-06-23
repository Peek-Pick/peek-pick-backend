package org.beep.sbpp.inquiries.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.common.BaseEntity2;
import org.beep.sbpp.inquiries.enums.InquiryStatus;
import org.beep.sbpp.inquiries.enums.InquiryType;
import org.beep.sbpp.users.entities.UserEntity;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_inquiry")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inquiry extends BaseEntity2 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id")
    private Long inquiryId;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private InquiryType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private InquiryStatus status;

    @Column(name = "is_delete", nullable = false)
    @JsonProperty("isDelete")
    private Boolean isDelete;

    @OneToMany(mappedBy = "inquiry", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<InquiryImage> images = new ArrayList<>();

    @Column(name ="mod_date")
    protected LocalDateTime modDate;

    @PrePersist
    public void prePersist() {
        this.regDate = LocalDateTime.now();
        this.modDate = LocalDateTime.now();
    }

//    status 변경 시 modDate가 변경되지 않게 수동 set으로 변경
//    -> setModDate(LocalDateTime.now())
//    @PreUpdate
//    public void preUpdate() {
//        this.modDate = LocalDateTime.now();
//    }

    public void addImage(InquiryImage img) {
        this.images.add(img);
        img.setInquiry(this);
    }
}
