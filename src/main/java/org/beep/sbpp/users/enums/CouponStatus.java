package org.beep.sbpp.users.enums;

public enum CouponStatus {

    AVAILABLE("사용 가능"),    // 사용 가능한 상태
    USED("사용 완료"),         // 이미 사용된 상태
    EXPIRED("만료됨");        // 유효기간 지난 상태

    private final String description;

    CouponStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
