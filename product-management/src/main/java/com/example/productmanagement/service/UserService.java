package com.example.productmanagement.service;
import com.example.productmanagement.dto.request.RegisterRequest;
import com.example.productmanagement.dto.request.UpdateUserRequest;
import com.example.productmanagement.dto.response.UserResponse;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserService extends UserDetailsService {

    // 我們將 register 方法也加入介面，這是更好的設計
    void register(RegisterRequest registerRequest);

    UserDetails loadUserByUserId(Integer userId);

    // --- 以下是您原有的 CRUD 方法宣告 ---
    

    List<UserResponse> getAllUsers();

    UserResponse getUserById(Integer id);

    UserResponse updateUser(Integer id,UpdateUserRequest request);

    void deleteUser(Integer id);
}
