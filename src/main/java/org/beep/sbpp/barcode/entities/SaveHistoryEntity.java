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
public class SaveHistoryEntity extends BaseEntity2 {
    @Id
    @Column(name = "view_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long viewId;

    @JoinColumn(name = "user_id")
    private Long userId;

    @JoinColumn(name = "product_id")
    private Long productId;

    @JsonProperty("isBarcode")
    private Boolean isBarcode;
}