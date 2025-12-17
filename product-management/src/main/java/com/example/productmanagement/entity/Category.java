package com.example.productmanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

@Getter
@Setter
@NoArgsConstructor // JPA 規範要求實體需有預設建構子
@Entity
@Table(name = "CATEGORIES")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Integer id; // 習慣上主鍵常命名為 id

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id") // 對應到資料庫中的 parent_category_id 欄位
    private Category parentCategory;

   
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Category> subCategories = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    

    // 可以加入一些方便操作關聯的方法
    public void addSubCategory(Category subCategory) {
        this.subCategories.add(subCategory);
        subCategory.setParentCategory(this);
    }

    public void removeSubCategory(Category subCategory) {
        this.subCategories.remove(subCategory);
        subCategory.setParentCategory(null);
    }
}