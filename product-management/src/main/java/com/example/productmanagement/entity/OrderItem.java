package com.example.productmanagement.entity;

import jakarta.persistence.*;
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
@NoArgsConstructor 
@Entity
@Table(name = "ORDER_ITEMS")
@EntityListeners(AuditingEntityListener.class)
public class OrderItem { // 【最佳實踐】類別名稱改為單數，代表一個訂單項目

    @Id // 【已修正】標記為主鍵
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 【已修正】設定主鍵生成策略
    @Column(name = "order_item_id")
    private Integer id;

    /**
     * 【最佳實踐】使用 @ManyToOne 建立與 Order 實體的關聯。
     * 一張訂單 (Order) 可以有多個訂單項目 (OrderItem)。
     * fetch = FetchType.LAZY 表示在需要時才載入關聯的 Order 物件，效能較好。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false) // 指定外鍵欄位
    private Order order;

    /**
     * 【最佳實踐】使用 @ManyToOne 建立與 Product 實體的關聯。
     * 一個產品 (Product) 可以出現在多個訂單項目中。
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false) // 指定外鍵欄位
    private Product product;

    // 以下欄位是為了"快照"下單當時的資訊，避免未來產品資訊變動影響歷史訂單，這是很好的設計。
    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "price_per_unit", nullable = false)
    private BigDecimal purchasePrice;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt; // 【已新增】訂單項目建立時間

   
    @LastModifiedDate // 【【【 修改 3：標註為更新時間 】】】
    @Column(nullable = false)
    private Instant updatedAt;

    // --- 自訂建構子 (方便建立物件) ---
    public OrderItem(Order order, Product product, Integer quantity,BigDecimal purchasePrice) {
        this.order = order;
        this.product = product;
        this.quantity = quantity;
        // 從關聯的 Product 物件中快照當前的名稱和價格
        this.productName = product.getName();
        this.purchasePrice = product.getPrice();
    }
}