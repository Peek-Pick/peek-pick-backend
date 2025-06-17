package org.beep.sbpp.admin.users.service;

import jakarta.transaction.Transactional;
import org.beep.sbpp.admin.users.dto.AdminUsersDetailResDTO;
import org.beep.sbpp.admin.users.dto.AdminUsersListResDTO;
import org.beep.sbpp.users.enums.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Transactional
public interface AdminUserService {

    // ============= admin =============
    // 사용자 목록 조회
    Page<AdminUsersListResDTO> getUserList(Pageable pageable, String category, String keyword, String status, Boolean social );

    // 사용자 디테일 조회
    AdminUsersDetailResDTO getUserDetail(Long userId);

    // 사용자 상태 변경
    void updateUserStatus(Long userId, String status, String banUntilStr);

}
