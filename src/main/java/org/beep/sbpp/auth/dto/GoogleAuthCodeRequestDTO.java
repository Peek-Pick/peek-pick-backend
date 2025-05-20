package org.beep.sbpp.auth.dto;

import lombok.Data;
import lombok.Getter;

@Getter
public class GoogleAuthCodeRequestDTO {
    private String code;
}
