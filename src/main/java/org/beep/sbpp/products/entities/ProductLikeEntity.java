package org.beep.sbpp.products.entities;

import jakarta.persistence.*;
import lombok.*;
import org.beep.sbpp.common.BaseEntity;
import org.beep.sbpp.users.entities.UserEntity;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tbl_product_like")
public class ProductLikeEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_like_id")
    private Long productLikeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity productEntity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity userEntity;

    @Column(name = "is_delete", nullable = false)
    private Boolean isDelete = false;
}
