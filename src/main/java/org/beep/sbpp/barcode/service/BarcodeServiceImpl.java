package org.beep.sbpp.barcode.service;

import lombok.RequiredArgsConstructor;
import org.beep.sbpp.barcode.entities.SaveHistoryEntity;
import org.beep.sbpp.barcode.repository.SaveHistoryRepository;
import org.beep.sbpp.products.entities.ProductEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class BarcodeServiceImpl implements BarcodeService {

    private final ProductRepository productRepository;
    private final SaveHistoryRepository historyRepo;

    @Override
    @Transactional
    public void saveHistoryByBarcode(String barcode, Long userId) {

        // 1) 상품 조회
        ProductEntity e = productRepository.findByBarcode(barcode)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "상품을 찾을 수 없습니다. 바코드=" + barcode
                        )
                );

        // 2) 히스토리 저장
        SaveHistoryEntity hist = SaveHistoryEntity.builder()
                .userId(userId)
                .productId(e.getProductId())
                .isBarcode(true)
                .build();
        historyRepo.save(hist);
    }
}
