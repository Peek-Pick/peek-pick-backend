package org.beep.sbpp.barcode.repository;

import org.beep.sbpp.barcode.entities.SaveHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaveHistoryRepository extends JpaRepository<SaveHistoryEntity, Long> {
}
