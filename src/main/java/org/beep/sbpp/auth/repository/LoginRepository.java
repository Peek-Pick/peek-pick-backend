package org.beep.sbpp.auth.repository;

import org.beep.sbpp.users.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<UserEntity, Long>  {
    Optional<UserEntity> findByEmailAndIsSocialTrue(String email);
    Optional<UserEntity> findByEmailAndIsSocialFalse(String email);
}
