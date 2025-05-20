package org.beep.sbpp.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionResultDTO<T> {

    private String result;

    private T data;

    public static <T> ActionResultDTO<T> success(T data) {
        return ActionResultDTO.<T>builder()
                .result("success")
                .data(data)
                .build();
    }

}
