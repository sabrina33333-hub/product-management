package com.example.productmanagement.controller;

import com.example.productmanagement.dto.response.AuthenticationResponse;
import com.example.productmanagement.dto.request.LoginRequest;
import com.example.productmanagement.dto.request.RegisterRequest;
import com.example.productmanagement.service.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")

@RequiredArgsConstructor
public class AuthController {

   
    private final AuthenticationService authenticationService;

    
    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(
            @RequestBody RegisterRequest request
    ) {
        
        return ResponseEntity.ok(authenticationService.register(request));
    }

    
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @RequestBody LoginRequest request
    ) {
        
        return ResponseEntity.ok(authenticationService.login(request));
    }

     @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request) {
        
        return ResponseEntity.ok("Logout successful");
    }
}
