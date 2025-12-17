package com.example.productmanagement.repository; // 請確認這是您專案的正確路徑

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.productmanagement.entity.User;

@Repository

public interface UserRepository extends JpaRepository<User, Integer> {

    Optional <User>findByEmail(String email);
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

}
