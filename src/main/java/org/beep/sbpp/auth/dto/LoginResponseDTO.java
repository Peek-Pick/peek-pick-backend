package org.beep.sbpp.auth.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.beep.sbpp.users.enums.Status;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {
    private String email;
    private String accessToken;
    private String refreshToken;

    @JsonProperty("isNew")
    private boolean isNew;

    private Status status;
    private LocalDate banUntil;
}
