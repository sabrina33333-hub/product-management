package com.example.productmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    // 這個方法專門捕捉 Spring Security 的認證錯誤，例如密碼錯誤
    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleAuthenticationException(AuthenticationException ex) {
        // 在伺服器控制台印出詳細的錯誤日誌
        System.err.println("!!! Authentication Failed: " + ex.getMessage());
        ex.printStackTrace();

        // 回傳一個清晰的 401 錯誤給前端
        Map<String, Object> body = Map.of(
            "status", HttpStatus.UNAUTHORIZED.value(),
            "error", "Unauthorized",
            "message", "Authentication failed: " + ex.getMessage() // 例如 "Bad credentials"
        );
        return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
    }

    // 這個方法捕捉所有其他未被處理的例外
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        // 在伺服器控制台印出詳細的錯誤日誌
        System.err.println("!!! An Unexpected Error Occurred: " + ex.getMessage());
        ex.printStackTrace();

        // 回傳一個通用的 500 錯誤給前端
        Map<String, Object> body = Map.of(
            "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "error", "Internal Server Error",
            "message", "An unexpected error occurred: " + ex.getClass().getSimpleName()
        );
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}