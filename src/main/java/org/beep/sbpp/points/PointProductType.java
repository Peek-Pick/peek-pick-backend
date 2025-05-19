package org.beep.sbpp.points;

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

}
