package org.beep.sbpp.points.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beep.sbpp.points.enums.PointLogsDesc;
import org.beep.sbpp.points.enums.PointLogsType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PointLogsDTO {

    private Long pointLogId;

    private int amount;

    private PointLogsType type;

    private PointLogsDesc description;


}
