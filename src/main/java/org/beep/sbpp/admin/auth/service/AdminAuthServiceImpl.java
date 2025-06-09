package org.beep.sbpp.admin.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.admin.auth.repository.AdminAuthRepository;
import org.beep.sbpp.admin.auth.entities.AdminEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Transactional
@Service
@Slf4j
@RequiredArgsConstructor
public class AdminAuthServiceImpl implements AdminAuthService {

    private final AdminAuthRepository adminAuthRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AdminEntity authenticate(String accountId, String password) {
        AdminEntity admin = adminAuthRepository.findByAccountId(accountId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 ID 또는 비밀번호가 올바르지 않습니다."));

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "관리자 ID 또는 비밀번호가 올바르지 않습니다.");
        }

        return admin;
    }

}
