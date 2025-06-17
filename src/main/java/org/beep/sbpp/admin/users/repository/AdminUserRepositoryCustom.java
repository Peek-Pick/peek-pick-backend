package org.beep.sbpp.admin.users.repository;

import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminUserRepositoryCustom {

    Page<UserEntity> findAllWithFilterAndSort(Pageable pageable, String category, String keyword, String status, Boolean social );
}
