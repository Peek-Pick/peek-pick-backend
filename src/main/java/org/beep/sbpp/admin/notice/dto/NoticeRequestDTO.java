package org.beep.sbpp.admin.notice.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeRequestDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private List<String> imgUrls;
}
