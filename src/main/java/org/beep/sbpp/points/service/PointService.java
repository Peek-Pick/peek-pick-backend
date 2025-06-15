package org.beep.sbpp.points.service;

import org.beep.sbpp.admin.points.dto.PointStoreListDTO;
import org.beep.sbpp.points.dto.PointLogsDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PointService {

    Page<PointStoreListDTO> list(String productType, Pageable pageable);

    int redeemPoints(Long userId, Long pointStoreId);

    Page<PointLogsDTO> pointLogsList(Long userId, Pageable pageable);
}
