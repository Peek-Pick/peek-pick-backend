package org.beep.sbpp.barcode.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.barcode.dto.ViewHistoryResponseDTO;
import org.beep.sbpp.barcode.entities.BarcodeHistoryEntity;
import org.beep.sbpp.barcode.repository.BarcodeHistoryRepository;
import org.beep.sbpp.products.entities.*;
import org.beep.sbpp.products.repository.ProductEnRepository;
import org.beep.sbpp.products.repository.ProductJaRepository;
import org.beep.sbpp.products.repository.ProductKoRepository;
import org.beep.sbpp.products.repository.ProductRepository;
import org.beep.sbpp.push.service.PushScheduleService;
import org.beep.sbpp.reviews.dto.ReviewAddDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class BarcodeServiceImpl implements BarcodeService {

    private final ProductRepository productRepository;
    private final ProductKoRepository koRepository;
    private final ProductEnRepository enRepository;
    private final ProductJaRepository jaRepository;
    private final BarcodeHistoryRepository barcodeHistoryRepository;
    private final PushScheduleService pushScheduleService;

    @Override
    public void saveHistoryByBarcode(String barcode, Long userId, String lang) {
        // 바코드 인식 시 푸시 예약과 히스토리 저장 모두 위임
        pushScheduleService.saveHistoryAndSchedulePush(userId, barcode, lang);
    }

    @Override
    public List<ViewHistoryResponseDTO> getRecentBarcodeViewHistory(Long userId, String lang) {
        // 1) 최신순·중복제거·최대 20건의 히스토리 조회
        List<BarcodeHistoryEntity> rawList = barcodeHistoryRepository
                .findRecentDistinctByUser(userId, PageRequest.of(0, 20));

        // 2) productId 리스트 추출
        List<Long> ids = rawList.stream()
                .map(BarcodeHistoryEntity::getProductId)
                .distinct()
                .toList();
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        // 3) BaseEntity를 한 번에 모두 로드
        Map<Long, ProductBaseEntity> baseMap = productRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(ProductBaseEntity::getProductId, Function.identity()));

        // 4) 언어별 Entity를 한 번에 모두 로드
        Map<Long, ProductLangEntity> langMap;
        switch (lang.toLowerCase().split("[-_]")[0]) {
            case "ko" -> langMap = koRepository.findAllById(ids).stream()
                    .collect(Collectors.toMap(ProductKoEntity::getProductId, Function.identity()));
            case "en" -> langMap = enRepository.findAllById(ids).stream()
                    .collect(Collectors.toMap(ProductEnEntity::getProductId, Function.identity()));
            case "ja" -> langMap = jaRepository.findAllById(ids).stream()
                    .collect(Collectors.toMap(ProductJaEntity::getProductId, Function.identity()));
            default -> throw new IllegalArgumentException("지원하지 않는 언어: " + lang);
        }

        // 5) DTO 매핑
        return rawList.stream()
                .map(history -> {
                    ProductBaseEntity base = baseMap.get(history.getProductId());
                    ProductLangEntity langE = langMap.get(history.getProductId());

                    return ViewHistoryResponseDTO.builder()
                            .viewId(history.getViewId())
                            .regDate(history.getRegDate())
                            .productId(base.getProductId())
                            .productName(langE.getName())
                            .productImageUrl(base.getImgUrl())
                            .barcode(base.getBarcode())
                            .isReview(history.getIsReview())
                            .userId(history.getUserId())
                            .build();
                })
                .toList();
    }
    @Override
    public void updateIsReview(ReviewAddDTO reviewAddDTO) {
        Long userId    = reviewAddDTO.getUserId();
        Long productId = reviewAddDTO.getProductId();

        if (!productRepository.existsById(productId)) {
            throw new EntityNotFoundException("해당 상품이 존재하지 않습니다. ID=" + productId);
        }

        List<BarcodeHistoryEntity> historyList = barcodeHistoryRepository
                .findTopByUserIdAndProductIdOrderByRegDateDesc(userId, productId, PageRequest.of(0, 1));
        if (historyList.isEmpty()) return;

        BarcodeHistoryEntity history = historyList.get(0);
        if (Boolean.TRUE.equals(history.getIsReview())) {
            log.info("이미 isReview=true 처리됨: userId={}, productId={}", userId, productId);
            return;
        }

        int updated = barcodeHistoryRepository
                .updateIsReviewForLatestBarcodeHistory(userId, productId, true);
        if (updated <= 0) {
            throw new IllegalArgumentException(
                    "isReview 업데이트 실패: userId=" + userId + ", productId=" + productId);
        }
        log.info("BarcodeHistory isReview=true 처리: userId={}, productId={}", userId, productId);
    }

    @Override
    public int countUnreviewedBarcodeHistory(Long userId) {
        int cnt = barcodeHistoryRepository
                .countDistinctProductByUserIdAndIsReviewFalse(userId);
        return Math.min(cnt, 20);
    }
}