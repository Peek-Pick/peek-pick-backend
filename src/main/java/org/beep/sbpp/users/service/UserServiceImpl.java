package org.beep.sbpp.users.service;

import lombok.RequiredArgsConstructor;
import org.beep.sbpp.points.entities.PointEntity;
import org.beep.sbpp.points.repository.PointRepository;
import org.beep.sbpp.tags.entities.TagEntity;
import org.beep.sbpp.tags.entities.TagUserEntity;
import org.beep.sbpp.tags.repository.TagRepository;
import org.beep.sbpp.tags.repository.TagUserRepository;
import org.beep.sbpp.users.dto.UserDTO;
import org.beep.sbpp.users.dto.UserProfileDTO;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.entities.UserProfileEntity;
import org.beep.sbpp.users.enums.Status;
import org.beep.sbpp.users.repository.UserProfileRepository;
import org.beep.sbpp.users.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserProfileRepository userProfileRepository;
    private final PasswordEncoder passwordEncoder;
    private final TagRepository tagRepository;
    private final TagUserRepository tagUserRepository;
    private final PointRepository pointRepository;

    @Override
    public Long signup(UserDTO dto) {

        if (userRepository.findByUserId(dto.getUserId()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        UserEntity user = UserEntity.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .isSocial(false)
                .isAdmin(false)
                .status(Status.ACTIVE) //ACTIVE를 default 값으로
                .build();

        userRepository.save(user);

        return user.getUserId();
    }

    @Override
    public Long profileRegister(Long userId, UserProfileDTO dto) {

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserProfileEntity profile = UserProfileEntity.builder()
                .user(user)
                .nickname(dto.getNickname())
                .gender(dto.getGender())
                .nationality(dto.getNationality())
                .birthDate(dto.getBirthDate())
                .profileImgUrl(dto.getProfileImgUrl())
                .build();

        userProfileRepository.save(profile);
        
        //포인트 데이터도 추가
        PointEntity pointEntity = PointEntity.builder()
                .user(user)
                .amount(0)
                .build();

        pointRepository.save(pointEntity);

        return profile.getUserId();
    }

    @Override
    public UserDTO userModify(UserDTO dto) {

        Optional<UserEntity> result = userRepository.findById(dto.getUserId());

        if (result.isEmpty()) {
            throw new IllegalArgumentException("해당 번호의 회원이 존재하지 않습니다.");
        }

        UserEntity user = result.get();

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(dto.getPassword());

        user.changePassword(encodedPassword);
        user.changeModDate(LocalDateTime.now());

        userRepository.save(user);

        return UserDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .password(user.getPassword())
                .isSocial(user.isSocial())
                .isAdmin(user.isAdmin())
                .status(user.getStatus())
                .regDate(user.getRegDate())
                .modDate(user.getModDate())
                .build();
    }

    @Override
    public Long userTagRegister(Long userId, List<Long> tagIdList) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        for (Long tagId : tagIdList) {
            TagEntity tag = tagRepository.findById(tagId)
                    .orElseThrow(() -> new IllegalArgumentException("Tag not found"));

            TagUserEntity tagUser = TagUserEntity.builder()
                    .user(user)
                    .tag(tag)
                    .build();

            tagUserRepository.save(tagUser);
        }

        return user.getUserId();
    }

}
