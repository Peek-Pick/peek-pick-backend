package org.beep.sbpp.users.service;

import jakarta.transaction.Transactional;
import org.beep.sbpp.users.dto.UserDTO;
import org.beep.sbpp.users.dto.UserProfileDTO;
import org.springframework.stereotype.Service;

@Service
@Transactional
public interface UserService {

    // 회원가입
    Long signup(UserDTO dto);

    // 프로필 등록
    Long profileRegister(Long userId, UserProfileDTO dto);

    // User 수정
    UserDTO userModify(UserDTO dto);
}
