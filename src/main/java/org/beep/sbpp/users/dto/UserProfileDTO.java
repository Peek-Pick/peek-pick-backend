package org.beep.sbpp.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beep.sbpp.users.enums.Gender;
import org.beep.sbpp.users.enums.Nationality;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    private Long userId;

    private String nickname;

    private Gender gender;

    private Nationality nationality;

    private LocalDate birthDate;

    private String profileImgUrl;

    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
