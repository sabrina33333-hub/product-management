
package com.example.productmanagement.repository;

import com.example.productmanagement.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
    // 根據分類名稱查找 (假設名稱是唯一的)
    Optional<Category> findByName(String name);
    
    //檢查類別名稱是否已存在
    boolean existsByName(String name);
    
    //查找所有頂層類別 (沒有父類別的類別)
    List<Category> findByParentCategoryIsNull();
    
   //根據父類別查找子類別
    List<Category> findByParentCategoryId(Integer parentCategoryId);
    
    //根據名稱模糊查詢類別
    @Query("SELECT c FROM Category c WHERE c.name LIKE %:name%")
    List<Category> findByNameContaining(@Param("name") String name);
    
    //檢查類別是否有關聯的產品 (用於刪除驗證)
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.category.id = :categoryId")
    boolean hasProducts(@Param("categoryId") Integer categoryId);
    
    //檢查類別是否有子類別 (用於刪除驗證)
    @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.parentCategory.id = :categoryId")
    boolean hasSubCategories(@Param("categoryId") Integer categoryId);
}
