package org.beep.sbpp.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {
    private String email;
    private String accessToken;
    private String refreshToken;

    @JsonProperty("isNew")
    private boolean isNew;
}
