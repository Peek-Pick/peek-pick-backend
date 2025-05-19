package org.beep.sbpp.users.service;

import jakarta.transaction.Transactional;
import org.beep.sbpp.users.dto.UserDTO;
import org.springframework.stereotype.Service;

@Service
@Transactional
public interface UserService {

    // 회원가입
    Long signup(UserDTO dto);

    // 모디파이
    UserDTO modify(UserDTO dto);
}
