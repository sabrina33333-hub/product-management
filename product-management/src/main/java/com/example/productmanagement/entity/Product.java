package com.example.productmanagement.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

import java.time.Instant;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

// --- Lombok 註解 ---
@Getter
@Setter
@NoArgsConstructor // 自動產生 JPA 需要的無參數建構子
// --- JPA 註解 ---
@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)//自動遞增
    @Column(name = "product_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id", nullable = false)
    private Vendor vendor; 

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category; 
    
    @NotBlank(message = "產品名稱不得為空")
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT") // 使用 TEXT 類型以儲存較長描述
    private String description;

    @Column(name = "image_url")
    private String imageUrl; // 【已新增】對應 get/set 方法的欄位

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @Column(name = "cost", nullable = false)
    private BigDecimal cost;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;
    
    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt; // 【已新增】建立時間欄位
    
    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt; // 【已新增】更新時間欄位

    private boolean isActive = true;


    // 如果需要，可以自訂一個方便建立物件的建構子
    public Product(Vendor vendor, Category category, String name, String description, 
        String imageUrl, BigDecimal price, BigDecimal cost,Integer stockQuantity) {
        this.vendor = vendor;
        this.category = category;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.price = price;
        this.cost = cost;
        this.stockQuantity = stockQuantity;
    }
}