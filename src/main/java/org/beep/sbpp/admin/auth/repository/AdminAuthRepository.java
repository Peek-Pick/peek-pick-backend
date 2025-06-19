package org.beep.sbpp.admin.auth.repository;

import org.beep.sbpp.admin.auth.entities.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminAuthRepository extends JpaRepository<AdminEntity, Long>  {
    Optional<AdminEntity> findByAccountId(String accountId);
}
