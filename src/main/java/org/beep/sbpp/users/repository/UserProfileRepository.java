package org.beep.sbpp.users.repository;

import org.beep.sbpp.users.entities.UserProfileEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfileEntity, Long> {

    Optional<UserProfileEntity> findByUserId(Long userId);

    boolean existsByNicknameAndUserIdNot(String nickname, Long userId);

    List<UserProfileEntity> findByNicknameContaining(String nickname);

    boolean existsByNickname(String nickname);
}
