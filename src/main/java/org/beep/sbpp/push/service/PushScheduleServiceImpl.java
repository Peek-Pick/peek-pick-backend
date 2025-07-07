package org.beep.sbpp.push.service;

import com.google.firebase.messaging.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.barcode.entities.BarcodeHistoryEntity;
import org.beep.sbpp.barcode.repository.BarcodeHistoryRepository;
import org.beep.sbpp.products.entities.ProductEntity;
import org.beep.sbpp.products.repository.ProductRepository;
import org.beep.sbpp.push.entities.FCMToken;
import org.beep.sbpp.push.entities.PushScheduleEntity;
import org.beep.sbpp.push.repository.FCMTokenRepository;
import org.beep.sbpp.push.repository.PushScheduleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PushScheduleServiceImpl implements PushScheduleService {

    private final FCMTokenRepository fcmTokenRepository;
    private final PushScheduleRepository pushScheduleRepository;
    private final BarcodeHistoryRepository barcodeHistoryRepository;
    private final ProductRepository productRepository;

    @Override
    public void saveFcmToken(Long userId, String token) {
        // 동일한 토큰이 이미 존재하면 저장하지 않음
        List<FCMToken> existingTokens = fcmTokenRepository.findByUserId(userId);
        boolean alreadyExists = existingTokens.stream()
                .anyMatch(t -> t.getToken().equals(token));

        if (alreadyExists) {
            System.out.println("[FCM] 이미 저장된 토큰: " + token);
            return;
        }

        // 기존 토큰 모두 삭제하고 새 토큰 저장 (중복 저장 방지용 로직 통일)
        fcmTokenRepository.deleteAll(existingTokens);

        fcmTokenRepository.save(
                FCMToken.builder()
                        .userId(userId)
                        .token(token)
                        .build()
        );

        System.out.println("[FCM] 새로운 토큰 저장됨: " + token);
    }

    @Override
    public void saveHistoryAndSchedulePush(Long userId, String barcode) {
        ProductEntity product = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found for barcode: " + barcode));

        barcodeHistoryRepository.save(
                BarcodeHistoryEntity.builder()
                        .userId(userId)
                        .productId(product.getProductId())
                        .isReview(false)
                        .build()
        );

        reservePush(
                userId,
                product.getProductId(),
                "Please write a review!",
                product.getName() + " Leave a product review.",
                "/products/" + product.getBarcode(),
                LocalDateTime.now().plusMinutes(3)  // TODO: 프로덕션은 30~60분으로 조정
        );
    }

    @Override
    public void reservePush(Long userId, Long productId, String title, String body, String url, LocalDateTime sendTime) {
        pushScheduleRepository.deleteByUserId(userId);  // 기존 예약 전부 삭제
        pushScheduleRepository.save(
                PushScheduleEntity.builder()
                        .userId(userId)
                        .productId(productId)
                        .title(title)
                        .body(body)
                        .url(url)
                        .sendTime(sendTime)
                        .build()
        );
    }

    @Override
    @Scheduled(fixedDelay = 60000)
    public void sendScheduledPushes() {
        List<PushScheduleEntity> dueSchedules = pushScheduleRepository.findBySendTimeBefore(LocalDateTime.now());

        for (PushScheduleEntity schedule : dueSchedules) {
            Long userId = schedule.getUserId();
            List<FCMToken> tokens = fcmTokenRepository.findByUserId(userId);

            if (tokens.isEmpty()) {
                System.out.println("[PUSH] No valid token → schedule deleted for userId: " + userId);
            } else {
                sendPushToTokens(tokens, schedule.getTitle(), schedule.getBody(), schedule.getUrl());
            }

            // 알림 성공/실패 여부 관계 없이 삭제 (한 번만 시도)
            pushScheduleRepository.delete(schedule);
        }
    }

    private boolean sendPushToTokens(List<FCMToken> tokens, String title, String body, String url) {
        boolean allSuccess = true;

        for (FCMToken token : tokens) {
            Message message = Message.builder()
                    .setToken(token.getToken())
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putData("url", url)
                    .build();

            try {
                String response = FirebaseMessaging.getInstance().send(message);
                System.out.println("[FCM] Sent successfully: " + response);
            } catch (FirebaseMessagingException e) {
                MessagingErrorCode errorCode = e.getMessagingErrorCode();

                if (errorCode == MessagingErrorCode.UNREGISTERED || errorCode == MessagingErrorCode.INVALID_ARGUMENT) {
                    // 이 토큰은 더 이상 유효하지 않음 → 삭제
                    System.out.println("[FCM] Invalid or unregistered token, deleting: " + token.getToken());
                    fcmTokenRepository.delete(token);
                } else {
                    // 일시적 실패 → 삭제하지 않고 로그만 남김
                    System.err.println("[FCM] Temporary failure when sending to token: " + token.getToken());
                    System.err.println(" → ErrorCode: " + errorCode + ", Message: " + e.getMessage());
                }

                allSuccess = false;
            } catch (Exception e) {
                // 기타 예외 (예: 네트워크 오류)
                System.err.println("[FCM] Unexpected error when sending to token: " + token.getToken());
                e.printStackTrace();
                allSuccess = false;
            }
        }

        return allSuccess;
    }

    @Override
    public boolean isFcmTokenValid(Long userId, String token) {
        List<FCMToken> tokens = fcmTokenRepository.findByUserId(userId);

        return tokens.stream()
                .anyMatch(t -> t.getToken().equals(token));
    }
}
