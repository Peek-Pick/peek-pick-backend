package org.beep.sbpp.users.repository;

import org.beep.sbpp.users.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {


    Optional<UserEntity> findByUserId(Long userId);

    Optional<UserEntity> findByEmail(String email);

}
