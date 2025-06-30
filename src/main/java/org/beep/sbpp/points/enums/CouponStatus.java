package org.beep.sbpp.points.enums;

public enum CouponStatus {

    AVAILABLE("Available"),    // 사용 가능한 상태
    USED("Used"),         // 이미 사용된 상태
    EXPIRED("Expired");        // 유효기간 지난 상태

    private final String description;

    CouponStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
