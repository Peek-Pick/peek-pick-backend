package org.beep.sbpp.users.repository;

import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByUserId(Long userId);

    Optional<UserEntity> findByEmail(String email);

    List<UserEntity> findByStatusAndBanUntilBefore(Status status, LocalDate time);
}
