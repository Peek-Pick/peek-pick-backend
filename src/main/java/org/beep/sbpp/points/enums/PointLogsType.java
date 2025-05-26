package org.beep.sbpp.points.enums;

public enum PointLogsType {

    EARN("획득"),
    USE("사용"),
    EXPIRED("만료됨");

    private final String displayName;

    PointLogsType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
