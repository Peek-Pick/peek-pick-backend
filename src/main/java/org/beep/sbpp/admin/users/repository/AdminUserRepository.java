package org.beep.sbpp.admin.users.repository;

import org.beep.sbpp.users.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminUserRepository extends JpaRepository<UserEntity, Long>,
AdminUserRepositoryCustom{


}
