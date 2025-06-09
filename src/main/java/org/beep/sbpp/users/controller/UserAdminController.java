package org.beep.sbpp.users.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.reviews.dto.ReviewSimpleDTO;
import org.beep.sbpp.reviews.service.ReviewService;
import org.beep.sbpp.users.dto.AdminUsersDetailResDTO;
import org.beep.sbpp.users.dto.AdminUsersListResDTO;
import org.beep.sbpp.users.dto.StatusUpdateDTO;
import org.beep.sbpp.users.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Slf4j
public class UserAdminController {

    private final UserService userService;
    private final ReviewService reviewService;


    // 사용자 전체 목록 조회
    @GetMapping("/list")
    public ResponseEntity<Page<AdminUsersListResDTO>> getList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("userId").descending());

        Page<AdminUsersListResDTO> result = userService.getUserList(pageable);

        return ResponseEntity.ok(result);
    }

    // 사용자 개별 프로필 조회
    @GetMapping("/{userId}")
    public ResponseEntity<AdminUsersDetailResDTO> getUserDetails(@PathVariable Long userId) {
        AdminUsersDetailResDTO result = userService.getUserDetail(userId);
        return ResponseEntity.ok(result);
    }

    // 사용자별 리뷰 전체 조회
    @GetMapping
    public ResponseEntity<Page<ReviewSimpleDTO>> getUserReviews(@RequestParam Long userId, @PageableDefault(sort = "regDate", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ReviewSimpleDTO> reviews = reviewService.getUserReviews(userId, pageable);

        return ResponseEntity.ok(reviews);
    }

    // 사용자별 리뷰 개수 조회
    @GetMapping("/count")
    public ResponseEntity<Long> getUserReviewCount(@RequestParam Long userId) {
        Long count = reviewService.countReviewsByUserId(userId);
        return ResponseEntity.ok(count);
    }

    // 사용자의 status 변경
    @PatchMapping("/{userId}/status")
    public ResponseEntity<Void> updateUserStatus(@PathVariable Long userId, @RequestBody String rawJson) throws JsonProcessingException {
        log.info("📦 Raw JSON: {}", rawJson);
        ObjectMapper mapper = new ObjectMapper();
        StatusUpdateDTO dto = mapper.readValue(rawJson, StatusUpdateDTO.class);
        log.info("✅ Parsed DTO: {}", dto);
        userService.updateUserStatus(userId, dto.getStatus(), dto.getBanUntil());
        return ResponseEntity.ok().build();
    }
}
