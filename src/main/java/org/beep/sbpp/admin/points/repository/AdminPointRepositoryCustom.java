package org.beep.sbpp.admin.points.repository;

import org.beep.sbpp.points.entities.PointStoreEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminPointRepositoryCustom {

    Page<PointStoreEntity> findAllWithFilterAndSort(Pageable pageable, String category, String keyword, Boolean hidden);
}
