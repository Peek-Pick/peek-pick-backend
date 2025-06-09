package org.beep.sbpp.users.service;

import jakarta.transaction.Transactional;
import org.beep.sbpp.users.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

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

    // ============= admin =============
    // 사용자 목록 조회
    Page<AdminUsersListResDTO> getUserList(Pageable pageable);

    // 사용자 디테일 조회
    AdminUsersDetailResDTO getUserDetail(Long userId);

    // 사용자 상태 변경
    void updateUserStatus(Long userId, String status, String banUntilStr);

}
