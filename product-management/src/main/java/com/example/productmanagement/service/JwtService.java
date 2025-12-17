package com.example.productmanagement.service;

import org.springframework.security.core.userdetails.UserDetails;
import java.util.Map;

public interface JwtService {
    // 從 Token 中提取使用者名稱
    String extractUserName(String token);

    // 為指定使用者產生 Token
    String generateToken(UserDetails userDetails);

    // 為指定使用者產生包含額外資訊的 Token
    String generateToken(Map<String, Object> extraClaims, UserDetails userDetails);

    // 驗證 Token 是否對該使用者有效
    boolean isTokenValid(String token, UserDetails userDetails);
}
