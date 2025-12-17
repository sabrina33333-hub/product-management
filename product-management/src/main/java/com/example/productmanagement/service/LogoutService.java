package com.example.productmanagement.service;

import com.example.productmanagement.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutService implements LogoutHandler {

    private final TokenRepository tokenRepository;

    @Override
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return; // 如果沒有 token，直接返回
        }
        jwt = authHeader.substring(7); // 提取 "Bearer " 後面的 token
        var storedToken = tokenRepository.findByToken(jwt)
                .orElse(null); // 在資料庫中尋找這個 token

        if (storedToken != null) {
            storedToken.setExpired(true); // 標記為過期
            storedToken.setRevoked(true); // 標記為撤銷
            tokenRepository.save(storedToken); // 儲存變更
        }
    }
}
