package org.beep.sbpp.users.service;

import jakarta.transaction.Transactional;
import org.beep.sbpp.users.dto.UserDTO;
import org.beep.sbpp.users.dto.UserProfileDTO;
import org.beep.sbpp.users.dto.UserSignupRequestDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public interface UserService {

    // 통합 회원 가입
    Long fullSignup(UserSignupRequestDTO dto);

    // 회원가입
    Long signup(UserDTO dto);

    // 프로필 등록
    Long profileRegister(Long userId, UserProfileDTO dto);

    // User 수정
    UserDTO userModify(UserDTO dto);

    // 태그 등록
    Long userTagRegister(Long userId, List<Long> tagIdList);
}
