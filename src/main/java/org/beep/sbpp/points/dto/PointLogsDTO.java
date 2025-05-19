package org.beep.sbpp.points.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.beep.sbpp.points.PointLogsDesc;
import org.beep.sbpp.points.PointLogsType;

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
