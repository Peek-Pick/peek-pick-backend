package org.beep.sbpp.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beep.sbpp.users.enums.Status;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusUpdateRequestDTO {
    private Status status;
}
