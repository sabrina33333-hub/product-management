package com.example.productmanagement.filter;

import com.example.productmanagement.service.JwtService;
import com.example.productmanagement.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 加上 @Component 註解，讓 Spring 能夠找到並管理這個類別
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;

    public JwtAuthenticationFilter(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // 1. 從請求的 Header 中取得 "Authorization"
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final Integer userId;

        // 2. 檢查 Header 是否存在，以及是否以 "Bearer " 開頭
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // 如果不符合，直接放行，讓後面的 Filter 處理 (通常會因為未認證而被拒絕)
            filterChain.doFilter(request, response);
            return;
        }

        // 3. 取得 "Bearer " 後面的 JWT Token 字串
        jwt = authHeader.substring(7);

        // 4. 從 JWT Token 中解析出使用者 userId
        userId = jwtService.extractUserId(jwt);

        // 5. 檢查是否已取得 ID，且當前的 SecurityContext 中沒有認證資訊
        //    (如果已經有認證資訊，代表之前的 Filter 已經處理過了，就不需要再處理)
        if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 6. 根據 ID 從資料庫中載入使用者詳細資訊
            UserDetails userDetails = this.userService.loadUserByUserId(userId);

            // 7. 驗證 Token 是否對該使用者有效
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // 8. 如果 Token 有效，建立一個認證通過的 Token
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // 我們不需要密碼
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // 9. 將這個認證通過的 Token 更新到 SecurityContextHolder 中
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // 10. 繼續執行後續的 Filter
        filterChain.doFilter(request, response);
    }
}
