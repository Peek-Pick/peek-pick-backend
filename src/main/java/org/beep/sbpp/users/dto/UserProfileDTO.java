package org.beep.sbpp.users.dto;

import org.beep.sbpp.users.entities.UserProfileEntity;
import org.beep.sbpp.users.enums.Gender;
import org.beep.sbpp.users.enums.Nationality;

import java.time.LocalDateTime;

public class UserProfileDTO {

    private String nickname;

    private Gender gender;

    private Nationality nationality;

    private String profileImgUrl;

    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
