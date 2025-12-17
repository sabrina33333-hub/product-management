package com.example.productmanagement.dto.response;
public record LoginResponse(
    String token,
    Integer userId // [NEW] 新增 userId 欄位
) {}
