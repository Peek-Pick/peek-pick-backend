package org.beep.sbpp.users.service;

import jakarta.transaction.Transactional;
import org.beep.sbpp.admin.users.dto.AdminUsersDetailResDTO;
import org.beep.sbpp.admin.users.dto.AdminUsersListResDTO;
import org.beep.sbpp.users.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
public interface UserService {

    // 통합 회원 가입
    Long fullSignup(UserSignupRequestDTO dto);

    // myPage 조회
    UserMyPageResponseDTO getUserMyPage(Long userId);

    // myPage Edit 조회
    UserMyPageEditResDTO getUserMyPageEdit(Long userId);

    // myPage Edit 수정
    void updateUserMyPage(Long userId, UserMyPageEditRequestDTO dto, MultipartFile file);

    // 비밀번호 확인
    void checkPassword(Long userId, PasswordCheckRequestDTO dto);

    // 닉네임 확인
    void chekNickname(Long userId, NicknameCheckRequestDTO dto);

    // 이메일 확인
    boolean isEmailExists(String email);

    // 닉네임 확인2
    boolean isNicknameExists(String nickname);

}
