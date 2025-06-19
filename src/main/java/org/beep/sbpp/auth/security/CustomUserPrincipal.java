package org.beep.sbpp.auth.security;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.security.Principal;

@Getter
@RequiredArgsConstructor
public class CustomUserPrincipal implements Principal {

    private final String userId;
    private final String email;
    private final String role;

    @Override
    public String getName() {
        return userId; // 혹은 email 등 사용 목적에 따라
    }
}
