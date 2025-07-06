package org.beep.sbpp.push.repository;

import org.beep.sbpp.push.entities.FCMToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FCMTokenRepository extends JpaRepository<FCMToken, Long> {
    List<FCMToken> findByUserId(Long userId);
}
