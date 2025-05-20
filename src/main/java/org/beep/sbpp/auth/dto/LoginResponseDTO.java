package org.beep.sbpp.auth.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponseDTO {
    private String accessToken;
    private String refreshToken;
}
