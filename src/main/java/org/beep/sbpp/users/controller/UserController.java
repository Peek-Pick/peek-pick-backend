package org.beep.sbpp.users.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.common.ActionResultDTO;
import org.beep.sbpp.users.dto.*;
import org.beep.sbpp.users.service.UserService;
import org.beep.sbpp.util.UserInfoUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserInfoUtil userInfoUtil;

    // 회원가입 풀세트
    @PostMapping("/signup")
    public ResponseEntity<ActionResultDTO<Long>> fullsignup(@RequestBody UserSignupRequestDTO dto) {
        log.info("Controller tagIdList: {}", dto);
        Long userId = userService.fullSignup(dto);
        return ResponseEntity.ok(ActionResultDTO.success(userId));
    }

    // myPage 조회
    @GetMapping("/mypage")
    public ResponseEntity<UserMyPageResponseDTO> getMyPage(HttpServletRequest request) {
        Long userId = userInfoUtil.getAuthUserId(request);

        UserMyPageResponseDTO dto = userService.getUserMyPage(userId);
        return ResponseEntity.ok(dto);
    }

    // 마이페이지 수정 조회
    @GetMapping("/mypage/edit")
    public ResponseEntity<UserMyPageResDTO> getMyPageE(
            HttpServletRequest request,
            @PathVariable String nickname) {

        Long authUserId = userInfoUtil.getAuthUserId(request);
        UserMyPageResDTO mypage = userService.getMyPageByNickname(nickname, authUserId);

        return ResponseEntity.ok(mypage);
    }

//    @PostMapping("/signup")
//    public ResponseEntity<ActionResultDTO<Long>> signup(UserDTO dto) {
//
//        Long userId = userService.signup(dto);
//
//        return ResponseEntity.ok(ActionResultDTO.success(userId));
//
//    }
//
//    @PostMapping("/signup/profile")
//    public ResponseEntity<ActionResultDTO<Long>> profileRegister(
//            @RequestParam("userId") Long userId,
//            @RequestBody UserProfileDTO dto) {
//
//        Long resultId = userService.profileRegister(userId, dto);
//        return ResponseEntity.ok(ActionResultDTO.success(resultId));
//    }
//
//    @PostMapping("/signup/tags")
//    public ResponseEntity<ActionResultDTO<Long>> userTagRegister(@RequestBody TagSelectionDTO dto){
//
//        Long resultUserTag = userService.userTagRegister(dto.getUserId(), dto.getTagIdList());
//
//        return ResponseEntity.ok(ActionResultDTO.success(resultUserTag));
//    }


//    @PutMapping("/{userId}")
//    public ResponseEntity<ActionResultDTO<Long>> userModify(
//            @PathVariable("userId") Long userId,
//            @RequestBody UserDTO dto){
//
//        dto.setUserId(userId);
//
//        UserDTO modified = userService.userModify(dto);
//
//        return ResponseEntity.ok(ActionResultDTO.success(modified.getUserId()));
//    }



}
