// src/main/java/org/beep/sbpp/common/PageResponse.java
package org.beep.sbpp.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {

    private List<T> content;
    private boolean hasNext;

    public static <T> PageResponse<T> of(List<T> content, boolean hasNext) {
        return new PageResponse<>(content, hasNext);
    }
}
