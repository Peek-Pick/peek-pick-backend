package org.beep.sbpp.auth.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.beep.sbpp.util.JWTException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@Slf4j
public class JWTControllerAdvice {

    @ExceptionHandler(JWTException.class)
    public ResponseEntity<Map<String, Object>> handleJWTException(JWTException e) {

        log.error("");
        log.error("+==================================");
        log.error("JWException " ,e);

        ResponseEntity<Map<String,Object>> result =
                ResponseEntity.status(401).body(Map.of("error", e.getMessage()));

        return result;
    }
}
