package org.beep.sbpp.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beep.sbpp.users.enums.Gender;
import org.beep.sbpp.users.enums.Nationality;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMyPageEditRequestDTO {

    private String password;

    private String nickname;

    @JsonProperty("profileImgUrl")
    private MultipartFile profileImgUrl;

    @JsonProperty("tagIdList")
    private List<Long> tagIdList;
}
