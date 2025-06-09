package org.beep.sbpp.tags.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagSelectionDTO {
// 사용자가 선택했던 태그들 전달할 때 사용하는 DTO
    private Long userId;

    private List<Long> tagIdList;

}
