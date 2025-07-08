package org.beep.sbpp.products.entities;

import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.tags.entities.TagEntity;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tbl_product_tag")
public class ProductTagEntity {
    @Id
    @Column(name = "product_tag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productTagId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private ProductBaseEntity productBaseEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id")
    private TagEntity tagEntity;

    @Column(name = "tag_count")
    private Integer tagCount;
}