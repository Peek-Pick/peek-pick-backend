package org.beep.sbpp.points.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum PointProductType {

    CU("CU"),
    GS25("GS25"),
    SEVEN_ELEVEN("세븐일레븐"),
    EMART24("이마트24"),
    OTHERS("기타");

    private final String displayName;

    PointProductType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @JsonCreator
    public static PointProductType from(String value) {
        for (PointProductType type : PointProductType.values()) {
            if (type.name().equalsIgnoreCase(value) || type.displayName.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown enum value: " + value);
    }
}
