package org.beep.sbpp.auth.service;

import org.beep.sbpp.auth.dto.LoginResponseDTO;

public interface MemberService {

    LoginResponseDTO handleGoogleLogin(String code);

}
