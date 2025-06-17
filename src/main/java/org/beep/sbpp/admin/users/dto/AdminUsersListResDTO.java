package org.beep.sbpp.admin.users.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beep.sbpp.users.enums.Status;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminUsersListResDTO {

    @JsonProperty("userId")
    private Long userId;

    private String email;

    @JsonProperty("isSocial")
    private boolean isSocial;

    private Status status;

    private LocalDate banUntil;

}
