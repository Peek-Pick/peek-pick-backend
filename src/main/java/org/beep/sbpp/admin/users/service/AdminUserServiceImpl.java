package org.beep.sbpp.admin.users.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.admin.users.dto.AdminUsersDetailResDTO;
import org.beep.sbpp.admin.users.dto.AdminUsersListResDTO;
import org.beep.sbpp.admin.users.repository.AdminUserRepository;
import org.beep.sbpp.points.repository.PointRepository;
import org.beep.sbpp.reviews.repository.ReviewRepository;
import org.beep.sbpp.tags.repository.TagRepository;
import org.beep.sbpp.tags.repository.TagUserRepository;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.entities.UserProfileEntity;
import org.beep.sbpp.users.enums.Status;
import org.beep.sbpp.users.repository.UserProfileRepository;
import org.beep.sbpp.users.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AdminUserServiceImpl implements AdminUserService {

    private final AdminUserRepository adminUserRepository;
    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final TagUserRepository tagUserRepository;

    // ============= Admin =============
    // 사용자 목록 조회
    @Override
    public Page<AdminUsersListResDTO> getUserList(Pageable pageable, String category, String keyword, String status, Boolean social) {

        Page<UserEntity> page = adminUserRepository.findAllWithFilterAndSort(pageable, category, keyword, status, social);

        return page.map(user -> {

            AdminUsersListResDTO.AdminUsersListResDTOBuilder builder = AdminUsersListResDTO.builder()
                    .userId(user.getUserId())
                    .email(user.getEmail())
                    .isSocial(user.isSocial())
                    .status(user.getStatus())
                    .banUntil(user.getBanUntil());
            return builder.build();

        });
    }

    // 사용자 상세 조회
    @Override
    public AdminUsersDetailResDTO getUserDetail(Long userId) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        UserProfileEntity profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Long> tagIdList = tagUserRepository.findByUserUserId(userId)
                .stream()
                .map(tu -> tu.getTag().getTagId())
                .collect(Collectors.toList());
        // log.info("태그 개수: {}", tagIdList.size());

        AdminUsersDetailResDTO dto = new AdminUsersDetailResDTO();
        dto.setNickname(profile.getNickname());
        dto.setEmail(user.getEmail());
        dto.setProfileImgUrl(profile.getProfileImgUrl());
        dto.setSocial(user.isSocial());
        dto.setGender(profile.getGender());
        dto.setNationality(profile.getNationality());
        dto.setBirthDate(profile.getBirthDate());
        dto.setStatus(user.getStatus());
        dto.setTagIdList(tagIdList);
        dto.setRegDate(user.getRegDate());

        return dto;
    }

    @Override
    public void updateUserStatus(Long userId, String status, String banUntilStr) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // 문자열 → LocalDateTime (또는 LocalDate) 파싱
        LocalDate banUntil = null;
        if (banUntilStr != null && !banUntilStr.isEmpty()) {
            banUntil = LocalDate.parse(banUntilStr);
        }

        log.info("statusString: {}", status);
        if (status != null && status.startsWith("BANNED")) {
            user.setStatus(Status.BANNED);
            user.setBanUntil(banUntil);
        } else {
            user.setStatus(Status.valueOf(status));
            user.setBanUntil(banUntil);
        }
    }

}
