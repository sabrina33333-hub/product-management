
package com.example.productmanagement.repository;

import com.example.productmanagement.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // 根據分類名稱查找 (假設名稱是唯一的)
    Optional<Category> findByName(String name);
}
