package org.beep.sbpp.points;

public enum PointLogsDesc {

    REVIEW_GENERAL("일반 리뷰 작성"),
    REVIEW_PHOTO("포토 리뷰 작성"),
    EVENT("이벤트 참여"),
    SHOP_USE("포인트 상점 사용"),
    EXPIRED("포인트 만료"),
    REFUND("포인트 환불"),
    OTHER("기타");

    private final String desc;

    PointLogsDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
