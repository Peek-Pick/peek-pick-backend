package org.beep.sbpp.push.service;

import java.time.LocalDateTime;

public interface PushScheduleService {
    // FCM 토큰 저장 (기존 토큰은 덮어쓰기 혹은 신규 저장)
    void saveFcmToken(Long userId, String token);

    // 바코드 인식 후 히스토리 저장 및 푸시 예약 호출
    void saveHistoryAndSchedulePush(Long userId, String barcode);

    // 예약된 푸시 스케줄 중 사용자별 최신만 남기고 예약 (이전 스케줄 삭제 포함)
    void reservePush(Long userId, Long productId, String title, String body, String url, LocalDateTime sendTime);

    // 스케줄러에서 1분(운영 60분)마다 호출: 예정된 푸시 발송 및 발송 완료 스케줄 삭제
    void sendScheduledPushes();

    // 토큰 검증
    boolean isFcmTokenValid(Long userId, String token);
}