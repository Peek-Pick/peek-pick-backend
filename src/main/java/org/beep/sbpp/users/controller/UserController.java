package org.beep.sbpp.users.controller;

import lombok.RequiredArgsConstructor;
import org.beep.sbpp.tags.dto.TagSelectionDTO;
import org.beep.sbpp.common.ActionResultDTO;
import org.beep.sbpp.users.dto.UserDTO;
import org.beep.sbpp.users.dto.UserProfileDTO;
import org.beep.sbpp.users.repository.UserRepository;
import org.beep.sbpp.users.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/signup")
    public ResponseEntity<ActionResultDTO<Long>> signup(UserDTO dto) {

        Long userId = userService.signup(dto);

        return ResponseEntity.ok(ActionResultDTO.success(userId));

    }

    @PostMapping("/signup/profile")
    public ResponseEntity<ActionResultDTO<Long>> profileRegister(
            @RequestParam("userId") Long userId,
            @RequestBody UserProfileDTO dto) {

        Long resultId = userService.profileRegister(userId, dto);
        return ResponseEntity.ok(ActionResultDTO.success(resultId));
    }

    @PostMapping("/signup/tags")
    public ResponseEntity<ActionResultDTO<Long>> userTagRegister(@RequestBody TagSelectionDTO dto){

        Long resultUserTag = userService.userTagRegister(dto.getUserId(), dto.getTagIdList());

        return ResponseEntity.ok(ActionResultDTO.success(resultUserTag));
    }


    @PutMapping("/{userId}")
    public ResponseEntity<ActionResultDTO<Long>> userModify(
            @PathVariable("userId") Long userId,
            @RequestBody UserDTO dto){

        dto.setUserId(userId);

        UserDTO modified = userService.userModify(dto);

        return ResponseEntity.ok(ActionResultDTO.success(modified.getUserId()));
    }
}
