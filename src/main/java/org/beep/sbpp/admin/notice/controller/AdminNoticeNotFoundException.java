package org.beep.sbpp.admin.notice.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 요청한 공지사항 ID를 찾을 수 없을 때 던지는 예외
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class AdminNoticeNotFoundException extends RuntimeException {

    public AdminNoticeNotFoundException(Long id) {
        super("공지사항을 찾을 수 없습니다. id: " + id);
    }
}
