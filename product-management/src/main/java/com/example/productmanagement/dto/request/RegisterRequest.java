package com.example.productmanagement.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;

// 假設你的 record 結構是這樣
public record RegisterRequest(
    @NotEmpty String username,
    @Email @NotEmpty String email,
    @NotEmpty String password
) {
    //  為 record 添加一個無參數的建構子
    // 這讓 model.addAttribute("user", new RegisterRequest()) 可以成功執行
    public RegisterRequest() {
        this(null, null, null);
    }
}