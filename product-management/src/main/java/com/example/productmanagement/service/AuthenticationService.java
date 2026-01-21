package com.example.productmanagement.service;

import com.example.productmanagement.dto.response.AuthenticationResponse;
import com.example.productmanagement.dto.request.LoginRequest;
import com.example.productmanagement.dto.request.RegisterRequest;
import com.example.productmanagement.entity.Token;
import com.example.productmanagement.entity.User;
import com.example.productmanagement.repository.UserRepository;
import com.example.productmanagement.repository.TokenRepository;
import com.example.productmanagement.repository.VendorRepository; 
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor // Lombok 會自動處理所有 final 欄位的建構子注入
public class AuthenticationService {

    // --- 原有的依賴 (保持不變) ---
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // --- 【【【 步驟 3：宣告新的依賴 】】】 ---
    // 我們需要 VendorRepository 來執行反向查找
    private final VendorRepository vendorRepository;


    // --- register 方法 (保持不變) ---
    public AuthenticationResponse register(RegisterRequest request) {
        
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
       

        var savedUser = userRepository.save(user);
        Map<String, Integer> a = new HashMap<>();
        a.put("userId",savedUser.getUserId());
        
        var jwtToken = jwtService.generateToken(a,user);
        saveUserToken(savedUser, jwtToken);

        
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(savedUser.getUserId())
                .build();
    }


    // --- 【【【 步驟 4：改造 login 方法！ 】】】 ---
    public AuthenticationResponse login(LoginRequest request) {
       
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()
                )
        );

        // 2. 根據 email 獲取 User 物件 (保持不變)
        var user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("找不到此信箱: " + request.email()));

        // 3. 【【【 執行反向查找！ 】】】
       
       // var vendorOpt = vendorRepository.findByUser(user);
        //Integer vendorId = vendorOpt.map(vendor -> Integer.valueOf(vendor.getVendorId())).orElse(null);
         
        // 4. 生成/更新 Token (保持不變)
        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        // 5. 【【【 回傳包含 vendorId 的最終情報！ 】】】
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .userId(user.getUserId()) // 回傳使用者 ID                
                .build();
    }

    
    // --- 私有輔助方法 (保持不變) ---
    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokensByUser(user.getUserId());
        if (validUserTokens.isEmpty())
            return;
        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });  
        tokenRepository.saveAll(validUserTokens);
    }
}
