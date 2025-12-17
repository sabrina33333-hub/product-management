package com.example.productmanagement.repository;

import com.example.productmanagement.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Integer> {

    // 根據使用者 ID 查詢所有有效的 Token
    @Query("""
        select t from Token t inner join User u on t.user.id = u.id
        where u.id = :userId and (t.expired = false or t.revoked = false)
    """)
    List<Token> findAllValidTokensByUser(Integer userId);

    // 根據 JWT 字串本身查詢 Token
    Optional<Token> findByToken(String token);
}
