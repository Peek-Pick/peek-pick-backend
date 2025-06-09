package org.beep.sbpp.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beep.sbpp.users.enums.Gender;
import org.beep.sbpp.users.enums.Nationality;
import org.beep.sbpp.users.enums.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUsersDetailResDTO {

    private String nickname;

    private String email;

    @JsonProperty("profileImgUrl")
    private String profileImgUrl;

    @JsonProperty("isSocial")
    private boolean isSocial;

    private Gender gender;

    private Nationality nationality;

    @JsonProperty("birthDate")
    private LocalDate birthDate;

    private Status status;

    @JsonProperty("tagIdList")
    private List<Long> tagIdList;

    @JsonProperty("regDate")
    private LocalDateTime regDate;
}
