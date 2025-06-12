package org.beep.sbpp.admin.notice.exception;

/**
 * 공지를 찾을 수 없을 때 던지는 예외
 */
public class AdminNoticeNotFoundException extends RuntimeException {
    public AdminNoticeNotFoundException(Long id) {
        super("공지사항을 찾을 수 없습니다. ID: " + id);
    }
}
