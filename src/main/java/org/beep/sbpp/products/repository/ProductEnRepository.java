package org.beep.sbpp.products.repository;

import org.beep.sbpp.products.entities.ProductEnEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductEnRepository extends JpaRepository<ProductEnEntity, Long> {
    // 필요시 커스텀 메서드 추가 가능
}
