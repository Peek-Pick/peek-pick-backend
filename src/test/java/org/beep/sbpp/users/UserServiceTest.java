package org.beep.sbpp.users;

import jakarta.transaction.Transactional;
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
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@Transactional
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserProfileRepository userProfileRepository;

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
