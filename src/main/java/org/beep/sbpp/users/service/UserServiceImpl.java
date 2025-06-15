package org.beep.sbpp.users.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnails;
import org.beep.sbpp.admin.users.dto.AdminUsersDetailResDTO;
import org.beep.sbpp.admin.users.dto.AdminUsersListResDTO;
import org.beep.sbpp.points.entities.PointEntity;
import org.beep.sbpp.points.repository.PointRepository;
import org.beep.sbpp.reviews.repository.ReviewRepository;
import org.beep.sbpp.tags.entities.TagEntity;
import org.beep.sbpp.tags.entities.TagUserEntity;
import org.beep.sbpp.tags.repository.TagRepository;
import org.beep.sbpp.tags.repository.TagUserRepository;
import org.beep.sbpp.users.dto.*;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.entities.UserProfileEntity;
import org.beep.sbpp.users.enums.Status;
import org.beep.sbpp.users.repository.UserProfileRepository;
import org.beep.sbpp.users.repository.UserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final TagRepository tagRepository;
    private final TagUserRepository tagUserRepository;
    private final PointRepository pointRepository;
    private final ReviewRepository reviewRepository;

    // 회원가입 풀세트
    @Override
    public Long fullSignup(UserSignupRequestDTO dto) {

        // 이메일 중복 검사 (비소셜 사용자만)
        if (!dto.isSocial() && userRepository.findByEmail(dto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email address already in use");
        }

        // 비밀번호 인코딩
        String encodedPassword = null;
        if (!dto.isSocial()) {
            if (dto.getPassword() == null || dto.getPassword().isBlank()) {
                throw new IllegalArgumentException("Password cannot be null or blank for non-social users");
            }
            encodedPassword = passwordEncoder.encode(dto.getPassword());
        }

        // UserEntity 저장
        UserEntity user = UserEntity.builder()
                .email(dto.getEmail())
                .password(encodedPassword)
                .isSocial(dto.isSocial())
                .status(Status.ACTIVE)
                .build();
        userRepository.save(user);

        // UserProfileEntity 저장
        UserProfileEntity profile = UserProfileEntity.builder()
                .user(user)
                .nickname(dto.getNickname())
                .gender(dto.getGender())
                .nationality(dto.getNationality())
                .birthDate(dto.getBirthDate())
                .profileImgUrl(dto.getProfileImgUrl())
                .build();
        userProfileRepository.save(profile);

        // TagUserEntity 저장
        if (dto.getTagIdList() != null) {
            for (Long tagId : dto.getTagIdList()) {
                TagEntity tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new IllegalArgumentException("Tag not found"));
                TagUserEntity tagUser = TagUserEntity.builder()
                        .user(user)
                        .tag(tag)
                        .build();
                tagUserRepository.save(tagUser);
            }
        }

        // PointEntity 저장
        PointEntity point = PointEntity.builder()
                .user(user)
                .amount(0)
                .build();
        pointRepository.save(point);

        return user.getUserId();
    }

    // myPage 조회
    @Override
    public UserMyPageResponseDTO getUserMyPage(Long userId) {

        UserProfileEntity profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        PointEntity point = pointRepository.findByUser_UserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Point not found"));

        UserMyPageResponseDTO dto = new UserMyPageResponseDTO();
        dto.setProfileImgUrl(profile.getProfileImgUrl());
        dto.setNickname(profile.getNickname());
        dto.setPoint(point.getAmount());

        dto.setWishlistedCount(9999);
        dto.setReviewCount(9999);
        dto.setCouponCount(9999);
        dto.setBarcodeHistoryCount(9999);

        return dto;
    }

    // myPage Edit 조회
    @Override
    public UserMyPageEditResDTO getUserMyPageEdit(Long userId) {

        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        UserProfileEntity profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        List<Long> tagIdList = tagUserRepository.findByUserUserId(userId)
                .stream()
                .map(tu -> tu.getTag().getTagId())
                .collect(Collectors.toList());
        log.info("태그 개수: {}", tagIdList.size());


        UserMyPageEditResDTO dto = new UserMyPageEditResDTO();
        dto.setEmail(user.getEmail());
        dto.setSocial(user.isSocial());
        dto.setNickname(profile.getNickname());
        dto.setGender(profile.getGender());
        dto.setNationality(profile.getNationality());
        dto.setBirthDate(profile.getBirthDate());
        dto.setProfileImgUrl(profile.getProfileImgUrl());
        dto.setTagIdList(tagIdList);

        return dto;
    }

    // myPage Edit 수정
    @Override
    public void updateUserMyPage(Long userId, UserMyPageEditRequestDTO dto, MultipartFile file) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        UserProfileEntity profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User profile not found"));

        //비밀번호 수정
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        // 닉네임 수정
        profile.setNickname(dto.getNickname());

        // 이미지 수정
        if (file != null && !file.isEmpty()) {
            String uuid = UUID.randomUUID().toString();
            String saveFileName = uuid + "_" + file.getOriginalFilename();
            String thumbFileName = "s_" + saveFileName;

            File target = new File("C:\\nginx-1.26.3\\html\\" + saveFileName);
            File thumbFile = new File("C:\\nginx-1.26.3\\html\\" + thumbFileName);

            try {
                // 원본 이미지 저장
                file.transferTo(target);

                //썸네일 생성
                Thumbnails.of(target)
                        .size(200, 200)
                        .toFile(thumbFile);

                // DB에는 썸네일 파일 이름만 저장
                profile.setProfileImgUrl(thumbFileName);

            } catch (Exception e) {
                log.error("이미지 업로드 실패: {}", e.getMessage());
                throw new RuntimeException("이미지 업로드 실패");
            }
        }// end if

        // 태그 수정 (기존 태그 삭제하고 새로 인설트)
        // 기존 태그 삭제
        tagUserRepository.deleteByUser_UserId(userId);
        // 새로운 태그 넣기
        if (dto.getTagIdList() != null) {
            for (Long tagId : dto.getTagIdList()) {
                TagEntity tag = tagRepository.findById(tagId)
                        .orElseThrow(() -> new IllegalArgumentException("Tag not found"));
                TagUserEntity tagUser = TagUserEntity.builder()
                        .user(user)
                        .tag(tag)
                        .build();
                tagUserRepository.save(tagUser);
            }// end for
        }// end if

        userRepository.save(user);
        userProfileRepository.save(profile);
    }

    // 비밀번호 확인
    @Override
    public void checkPassword(Long userId, PasswordCheckRequestDTO dto) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        boolean isMatch = passwordEncoder.matches(dto.getPassword(), user.getPassword());

        if (!isMatch) {
            throw new BadCredentialsException("Password does not match");
        }
    }

    // 닉네임 중복 확인
    @Override
    public void chekNickname(Long userId, NicknameCheckRequestDTO dto) {

        // 다른 사용자가 이 닉네임을 사용하고 있는지 확인
        boolean exists = userProfileRepository
                            .existsByNicknameAndUserIdNot(dto.getNickname(), userId);

        if (exists) {
            throw new DuplicateKeyException("It has already been in use");
        }
    }

    // 이메일 확인
    @Override
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    //닉네임 확인2
    @Override
    public boolean isNicknameExists(String nickname) {
        return userProfileRepository.existsByNickname(nickname);
    }


}

