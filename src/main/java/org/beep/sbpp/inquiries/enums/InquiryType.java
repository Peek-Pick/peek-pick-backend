package org.beep.sbpp.inquiries.enums;

import lombok.Getter;

@Getter
public enum InquiryType {
    ACCOUNT("계정/로그인"),
    PRODUCT_ADD("상품 추가"),
    POINT_REVIEW("포인트/리뷰"),
    HOW_TO_USE("사용 방법"),
    BUG("오류/버그"),
    ETC("기타 문의");

    private final String description;

    InquiryType(String description) {
        this.description = description;
    }
}