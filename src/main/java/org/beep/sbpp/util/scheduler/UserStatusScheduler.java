package org.beep.sbpp.util.scheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.enums.Status;
import org.beep.sbpp.users.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserStatusScheduler {

    private final UserRepository userRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // 자정마다 (cron = "초 분 시 일 월 요일")
    public void unbanUsers() {
        log.info("✅ 스케줄러 작동: " + LocalDateTime.now());
        List<UserEntity> expired = userRepository.findByStatusAndBanUntilBefore(Status.BANNED, LocalDate.now());
        for (UserEntity user : expired) {
            user.setStatus(Status.ACTIVE);
            user.setBanUntil(null);
        }
        userRepository.saveAll(expired);
    }
}
