package org.beep.sbpp.products.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

/**
 * Composite key for ProductLocaleEntity.
 */
@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ProductLocaleId implements Serializable {

    @Column(name = "product_id")
    private Long productId;

    /** Language code, e.g. 'KR', 'US'. */
    @Column(name = "lang", length = 10)
    private String lang;
}
