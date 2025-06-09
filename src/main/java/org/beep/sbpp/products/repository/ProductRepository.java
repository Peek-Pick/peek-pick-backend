package org.beep.sbpp.products.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.beep.sbpp.products.entities.ProductEntity;

import java.util.Optional;

public interface ProductRepository
        extends JpaRepository<ProductEntity, Long>, ProductRepositoryCustom {

    Optional<ProductEntity> findByBarcode(String barcode);

}
