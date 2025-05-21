package org.beep.sbpp.reviews.enums;

import lombok.Getter;

@Getter
public enum ReportReason {
    POLITICS("정치"),
    HATE("혐오"),
    DEFAMATION("비방"),
    PROFANITY("욕설");

    private final String description;

    ReportReason(String description) {
        this.description = description;
    }
}