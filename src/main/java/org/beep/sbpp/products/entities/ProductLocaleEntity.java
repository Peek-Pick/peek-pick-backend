package org.beep.sbpp.products.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * Language dependent product details.
 */
@Entity
@Table(name = "tbl_product_locale")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductLocaleEntity {

    @EmbeddedId
    private ProductLocaleId id;

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductBaseEntity productBase;

    /** Localized product name */
    @Column(name = "name", length = 255)
    private String name;

    /** Localized description */
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    /** Localized category */
    @Column(name = "category", length = 255)
    private String category;
}
