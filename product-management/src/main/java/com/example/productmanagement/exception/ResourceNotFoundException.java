package com.example.productmanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// @ResponseStatus 註解會告訴 Spring Boot：
// 當這個 Exception 被拋出且未被捕獲時，
// HTTP 回應的狀態碼應該是 404 NOT FOUND。
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    // 這是一個 Java 的慣例，用於序列化
    private static final long serialVersionUID = 1L;

    // 這就是我們缺少的建構子！
    // 它接收一個錯誤訊息字串...
    public ResourceNotFoundException(String message) {
        // ...然後把這個訊息傳遞給父類別 RuntimeException
        super(message);
    }
}
