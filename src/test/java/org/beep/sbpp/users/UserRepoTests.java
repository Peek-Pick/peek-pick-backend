package org.beep.sbpp.users;


import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.points.dto.UserCouponDTO;
import org.beep.sbpp.points.repository.UserCouponRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Slf4j
@TestPropertySource(properties = {
        "GOOGLE_CLIENT_ID=test-client-id",
        "GOOGLE_CLIENT_SECRET=test-secret",
        "GOOGLE_REDIRECT_URI=http://localhost:8080/oauth2/callback"
})
public class UserRepoTests {

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Test
    public void listUserCoupons() {

        Pageable pageable = PageRequest.of(0, 10, Sort.by("couponId").descending());

        Page<UserCouponDTO> result = userCouponRepository.couponList(10L, pageable);

        result.forEach(arr -> log.info(arr.toString()));
    }

}
