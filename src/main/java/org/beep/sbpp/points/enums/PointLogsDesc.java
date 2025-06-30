package org.beep.sbpp.points.enums;

public enum PointLogsDesc {

    REVIEW_GENERAL("Write general review"), // 일반 리뷰 작성
    REVIEW_PHOTO("Write photo review"), // 포토 리뷰 작성
    EVENT("Participate in event"), // 이벤트 참여
    SHOP_USE("Use point shop"), // 포인트 상점 사용
    EXPIRED("Points expired"), // 포인트 만료
    REFUND("Points refunded"), // 포인트 환불
    OTHER("Other"); // 기타

    private final String desc;

    PointLogsDesc(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
