package org.beep.sbpp.products.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.products.entities.ProductBaseEntity;
import org.beep.sbpp.products.entities.ProductLikeEntity;
import org.beep.sbpp.products.repository.ProductLikeRepository;
import org.beep.sbpp.products.repository.ProductRepository;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.repository.UserRepository;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ProductLikeServiceImpl implements ProductLikeService {
    private final ProductLikeRepository productLikeRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public Long toggleProductLike(Long productId, Long userId) {
        var opt = productLikeRepository
                .findByProductEntity_ProductIdAndUserEntity_UserId(productId, userId);

        if (opt.isPresent()) {
            ProductLikeEntity like = opt.get();
            if (like.getIsDelete()) {
                productLikeRepository.activateLike(productId, userId);
                productLikeRepository.increaseLikeCount(productId);
                log.info("Activated like: productId={}, userId={}", productId, userId);
            } else {
                productLikeRepository.deactivateLike(productId, userId);
                productLikeRepository.decreaseLikeCount(productId);
                log.info("Deactivated like: productId={}, userId={}", productId, userId);
            }
            return like.getProductLikeId();
        }

        // 신규 좋아요
        ProductBaseEntity product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품 없음: " + productId));
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음: " + userId));

        ProductLikeEntity newLike = ProductLikeEntity.builder()
                .productEntity(product)
                .userEntity(user)
                .isDelete(false)
                .build();
        productLikeRepository.save(newLike);
        productLikeRepository.increaseLikeCount(productId);
        log.info("New like created: productId={}, userId={}", productId, userId);
        return newLike.getProductLikeId();
    }

    @Override
    public boolean hasUserLikedProduct(Long productId, Long userId) {
        return productLikeRepository.hasUserLikedProduct(productId, userId);
    }
}