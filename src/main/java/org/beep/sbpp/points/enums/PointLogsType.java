package org.beep.sbpp.points.enums;

public enum PointLogsType {

    EARN("Earn"), // 획득
    USE("Used"), // 사용
    EXPIRED("Expired"); // 만료됨

    private final String displayName;

    PointLogsType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
