package org.beep.sbpp.admin.auth.controller;

import org.beep.sbpp.admin.exception.AdminAccessDeniedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AdminAccessDeniedException.class)
    public ResponseEntity<String> handleAdminAccessDenied(AdminAccessDeniedException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    // 추가적으로 다른 예외 처리도 여기에 넣으면 좋아
}