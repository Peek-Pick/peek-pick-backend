package org.beep.sbpp.users;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.tags.entities.TagEntity;
import org.beep.sbpp.tags.entities.TagUserEntity;
import org.beep.sbpp.tags.repository.TagRepository;
import org.beep.sbpp.tags.repository.TagUserRepository;
import org.beep.sbpp.users.dto.UserDTO;
import org.beep.sbpp.users.dto.UserProfileDTO;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.enums.Gender;
import org.beep.sbpp.users.enums.Nationality;
import org.beep.sbpp.users.repository.UserProfileRepository;
import org.beep.sbpp.users.repository.UserRepository;
import org.beep.sbpp.users.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Slf4j
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserProfileRepository userProfileRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private TagUserRepository tagUserRepository;

    @Test
    @Commit
    void testSignup(){

        for (int i = 1; i <= 9; i++) {
            UserDTO dto = UserDTO.builder()
                    .email("test"+i+i+"@test.com")
                    .password("1234")
                    .build();

            Long userId = userService.signup(dto);

            assertTrue(userRepository.findByUserId(userId).isPresent());
        }

    }

    @Test
    @Commit
    void testProfileRegister() {

        for (int i = 1; i <= 9; i++) {

            Long userId = (long) i;

            UserEntity user = userRepository.findById(userId)
                    .orElseThrow( () -> new IllegalArgumentException("User Not Found"));

            UserProfileDTO dto = UserProfileDTO.builder()
                    .nickname("testNickname"+i)
                    .gender(Gender.FEMALE)
                    .nationality(Nationality.KR)
                    .birthDate(LocalDate.of(2000, 7, i))
                    .build();

            userService.profileRegister(userId, dto);

            assertTrue(userProfileRepository.findByUserId(userId).isPresent());
        }

    }

    @Test
    @Commit
    void testUserTagRegister(){

        List<TagEntity> tagList = tagRepository.findAll();

        for (int i = 1; i <= 9; i++) {
            Long userId = (long) i;

            // 사용자 있는지 먼저 확인
            assertTrue(userRepository.findByUserId(userId).isPresent());

            // 각 사용자에게 태그 랜덤 3개 넣기
            Collections.shuffle(tagList);
            List<Long> tagIdList = tagList.stream().limit(3).map(TagEntity::getTagId).collect(Collectors.toList());

            userService.userTagRegister(userId, tagIdList);

            // 태그 들어가졌는지 확인
            List<TagUserEntity> tagUsers = tagUserRepository.findByUser_UserId(userId);
            assertEquals(3, tagUsers.size());

            tagUsers.forEach(tagUser ->
                    log.info("userId = {}, tag{}", tagUser.getUser(), tagUser.getTag().getTagName()));

        }

    }

    @Test
    @Commit
    void testUserModi(){

        Optional<UserEntity> result = userRepository.findByUserId(1L);

        if (result.isPresent()){
            UserEntity user = result.get();

            String encoded = passwordEncoder.encode("1111");
            user.changePassword(encoded);

            userRepository.save(user);
        } else {
            fail("User not found");
        }
    }


}
