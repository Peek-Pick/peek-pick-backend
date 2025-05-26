package org.beep.sbpp.users.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beep.sbpp.users.enums.Gender;
import org.beep.sbpp.users.enums.Nationality;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMyPageResDTO {

    private String email;

    private String nickname;

    private LocalDate birthday;

    private Nationality nationality;

    private Gender gender;

    private String profileImgUrl;

    private List<String> tags;

    public UserMyPageResDTO(String email, String nickname, LocalDate birthday,
                            Nationality nationality, Gender gender, String profileImgUrl) {
        this.email = email;
        this.nickname = nickname;
        this.birthday = birthday;
        this.nationality = nationality;
        this.gender = gender;
        this.profileImgUrl = profileImgUrl;
        this.tags = new ArrayList<>();
    }
}
