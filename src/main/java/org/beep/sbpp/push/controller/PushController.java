package org.beep.sbpp.push.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.beep.sbpp.push.service.PushScheduleService;
import org.beep.sbpp.util.UserInfoUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/push")
@RequiredArgsConstructor
public class PushController {

    private final PushScheduleService pushScheduleService;
    private final UserInfoUtil userInfoUtil;

    @PostMapping("/fcm")
    public ResponseEntity<?> saveFcmToken(@RequestBody Map<String, String> body, HttpServletRequest request) {
        Long userId = userInfoUtil.getAuthUserId(request);
        String token = body.get("token");
        pushScheduleService.saveFcmToken(userId, token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/fcm/validate")
    public ResponseEntity<Map<String, Boolean>> validateFcmToken(@RequestBody Map<String, String> body, HttpServletRequest request) {
        Long userId = userInfoUtil.getAuthUserId(request);
        String token = body.get("token");

        boolean valid = pushScheduleService.isFcmTokenValid(userId, token);

        return ResponseEntity.ok(Map.of("valid", valid));
    }
}
