package org.beep.sbpp.points.service;

import org.beep.sbpp.admin.points.dto.PointStoreDTO;
import org.beep.sbpp.admin.points.dto.PointStoreListDTO;
import org.beep.sbpp.points.dto.PointLogsDTO;
import org.beep.sbpp.points.enums.PointLogsDesc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PointService {

    Page<PointStoreListDTO> list(String productType, Pageable pageable);

    PointStoreDTO read(Long pointstoreId);

    int redeemPoints(Long userId, Long pointStoreId);

    int earnPoints(Long userId, int earnAmount, PointLogsDesc description);

    Page<PointLogsDTO> pointLogsList(Long userId, Pageable pageable);

    Integer getUserPointAmount(Long userId);
}
