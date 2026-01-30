package com.example.productmanagement.repository;

import com.example.productmanagement.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {


    Optional<Product> findByName(String name);
    boolean existsByName(String name);
    List<Product> findByNameContainingIgnoreCase(String keyword);
    List<Product> findByPriceLessThan(BigDecimal price);
    List<Product> findByStockQuantityEquals(Integer stockQuantity);

    
     List<Product> findByCategoryName(String categoryName);

   
    @Query("SELECT p FROM Product p WHERE p.category.name = :categoryName")
    List<Product> findByCustomCategory(@Param("categoryName") String categoryName);
    
    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") Integer categoryId);
    
    List<Product> findByStockQuantityLessThan(int threshold);

    @Query("SELECT p FROM Product p JOIN FETCH p.category JOIN FETCH p.vendor WHERE p.id = :id")
    Optional<Product> findByIdWithAssociations(@Param("id") Integer id);

}