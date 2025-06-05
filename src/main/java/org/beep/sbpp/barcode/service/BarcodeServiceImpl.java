package org.beep.sbpp.barcode.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.barcode.dto.ViewHistoryResponseDTO;
import org.beep.sbpp.barcode.entities.BarcodeHistoryEntity;
import org.beep.sbpp.barcode.repository.BarcodeHistoryRepository;
import org.beep.sbpp.products.entities.ProductEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.beep.sbpp.reviews.dto.ReviewAddDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BarcodeServiceImpl implements BarcodeService {

    private final ProductRepository productRepository;
    private final BarcodeHistoryRepository barcodeHistoryRepository;

    @Override
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
        BarcodeHistoryEntity hist = BarcodeHistoryEntity.builder()
                .userId(userId)
                .productId(e.getProductId())
                .isBarcodeHistory(true)
                .isReview(false)
                .build();
        barcodeHistoryRepository.save(hist);
    }

    @Override
    public List<ViewHistoryResponseDTO> getRecentBarcodeViewHistory(Long userId) {
        // 사용자 기준, 중복 product_id 제거, barcode=true, 최신순, 최대 20개
        List<BarcodeHistoryEntity> rawList = barcodeHistoryRepository.findRecentDistinctByUser(userId);

        return rawList.stream().map(history -> {
            ProductEntity product = productRepository.findById(history.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("상품 정보를 찾을 수 없습니다."));

            return ViewHistoryResponseDTO.builder()
                    .viewId(history.getViewId())
                    .regDate(history.getRegDate())
                    .productId(product.getProductId())
                    .productName(product.getName())
                    .productImageUrl(product.getImgUrl())
                    .isBarcodeHistory(history.getIsBarcodeHistory())
                    .isReview(history.getIsReview())
                    .barcode(product.getBarcode())
                    .userId(history.getUserId())
                    .build();
        }).collect(Collectors.toList());
    }

    @Override
    public void updateIsReview(ReviewAddDTO reviewAddDTO) {

        Long userId = reviewAddDTO.getUserId();
        Long productId = reviewAddDTO.getProductId();

        // 상품 존재 확인
        if (!productRepository.existsById(productId)) {
            throw new EntityNotFoundException("해당 상품이 존재하지 않습니다.");
        }

        // 최신 바코드 조회내역 1건 조회
        List<BarcodeHistoryEntity> historyList = barcodeHistoryRepository
                .findLatestBarcodeHistoryByUserAndProduct(userId, productId, PageRequest.of(0, 1));

        if (historyList.isEmpty()) {
            return; // 내역 없으면 아무 작업도 하지 않음
        }

        BarcodeHistoryEntity history = historyList.get(0);

        if (Boolean.TRUE.equals(history.getIsReview())) {
            log.info("Already marked isReview=true. Skip update.");
            return;
        }

        // 리뷰 여부 업데이트
        int result = barcodeHistoryRepository.updateIsReviewForLatestBarcodeHistory(userId, productId, true);

        if (result <= 0) {
            throw new IllegalArgumentException("Failed to update isReview for userId=" + userId + ", productId=" + productId);
        }

        log.info("Updated BarcodeHistory: userId={} productId={} isReview=true", userId, productId);
    }
}