package org.beep.sbpp.users;

import jakarta.transaction.Transactional;
import org.beep.sbpp.users.dto.UserDTO;
import org.beep.sbpp.users.entities.UserEntity;
import org.beep.sbpp.users.repository.UserRepository;
import org.beep.sbpp.users.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Commit;

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

    @Test
    @Commit
    void testSignup(){

        for (int i = 1; i <= 9; i++) {
            UserDTO dto = UserDTO.builder()
                    .email("test"+i+i+"@test.com")
                    .password("1234")
                    .build();

            Long userId = userService.signup(dto);

            assertTrue(userRepository.findById(userId).isPresent());
        }

    }


    @Test
    @Commit
    void testUserModi(){

        Optional<UserEntity> result = userRepository.findById(1L);

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
