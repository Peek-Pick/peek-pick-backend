package org.beep.sbpp.users.scheduler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.enums.Status;
import org.beep.sbpp.users.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserStatusScheduler {

    private final UserRepository userRepository;

    @Transactional
    @Scheduled(cron = "0 0 0 * * *") // 자정마다 (cron = "초 분 시 일 월 요일")
    public void unbanUsers() {
        List<UserEntity> expired = userRepository.findByStatusAndBanUntilBefore(Status.BANNED, LocalDate.now());
        for (UserEntity user : expired) {
            user.setStatus(Status.ACTIVE);
            user.setBanUntil(null);
        }
        userRepository.saveAll(expired);
    }
}
