package org.beep.sbpp.users.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.tags.dto.TagSelectionDTO;
import org.beep.sbpp.common.ActionResultDTO;
import org.beep.sbpp.users.dto.UserDTO;
import org.beep.sbpp.users.dto.UserProfileDTO;
import org.beep.sbpp.users.dto.UserSignupRequestDTO;
import org.beep.sbpp.users.repository.UserRepository;
import org.beep.sbpp.users.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ActionResultDTO<Long>> fullsignup(@RequestBody UserSignupRequestDTO dto) {
        log.info("Controller tagIdList: {}", dto);
        Long userId = userService.fullSignup(dto);
        return ResponseEntity.ok(ActionResultDTO.success(userId));
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


    @PutMapping("/{userId}")
    public ResponseEntity<ActionResultDTO<Long>> userModify(
            @PathVariable("userId") Long userId,
            @RequestBody UserDTO dto){

        dto.setUserId(userId);

        UserDTO modified = userService.userModify(dto);

        return ResponseEntity.ok(ActionResultDTO.success(modified.getUserId()));
    }



}
