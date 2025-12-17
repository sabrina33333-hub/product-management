package com.example.productmanagement.entity;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;
// 確保 import 指向正確的 repository 套件
import com.example.productmanagement.repository.UserRepository;

@Component
public class UserHealthIndicator implements HealthIndicator {

    private final UserRepository userRepository;

    // Spring 會自動從容器中尋找 UserRepository Bean 並注入
    public UserHealthIndicator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Health health() {
        long userCount = userRepository.count();
        
        final long EXPECTED_USER_COUNT = 3;

        if (userCount >= EXPECTED_USER_COUNT) {
            return Health.up()
                         .withDetail("userCount", userCount)
                         .build();
        } else {
            return Health.down()
                         .withDetail("userCount", userCount)
                         .withDetail("reason", "User count is " + userCount + ", which is different from the expected " + EXPECTED_USER_COUNT)
                         .build();
        }
    }
}