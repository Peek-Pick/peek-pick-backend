package org.beep.sbpp.users.dto;

import lombok.*;
import org.beep.sbpp.users.enums.Status;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long userId;

    private String email;

    private String password;

    private boolean isSocial;

    private boolean isAdmin;

    private Status status;

    private LocalDateTime regDate;
    private LocalDateTime modDate;
}
