package org.beep.sbpp.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beep.sbpp.users.enums.Gender;
import org.beep.sbpp.users.enums.Nationality;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMyPageEditResDTO {

    private String email;

    @JsonProperty("isSocial")
    private boolean isSocial;

    private String nickname;
    private Gender gender;
    private Nationality nationality;

    @JsonProperty("birthDate")
    private LocalDate birthDate;

    @JsonProperty("profileImgUrl")
    private String profileImgUrl;

    @JsonProperty("tagIdList")
    private List<Long> tagIdList;
}
