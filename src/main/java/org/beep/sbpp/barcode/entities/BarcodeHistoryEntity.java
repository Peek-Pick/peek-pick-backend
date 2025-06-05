package org.beep.sbpp.barcode.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.common.BaseEntity2;

@Entity
@Table(name = "tbl_view_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BarcodeHistoryEntity extends BaseEntity2 {
    @Id
    @Column(name = "view_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long viewId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "product_id", nullable = false)
    private Long productId;

    @Column(nullable = false)
    @JsonProperty("isBarcodeHistory")
    private Boolean isBarcodeHistory;

    @Column(nullable = false)
    @JsonProperty("isReview")
    private Boolean isReview;
}