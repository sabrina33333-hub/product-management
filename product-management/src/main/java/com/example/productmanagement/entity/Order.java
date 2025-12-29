package com.example.productmanagement.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.example.productmanagement.model.OrderStatus;

// --- Lombok 註解 ---
@Getter 
@Setter
@NoArgsConstructor 
@EntityListeners(AuditingEntityListener.class) 
@Entity
@Table(name = "ORDERS")

public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    //與顧客(User)實體的關聯。一個顧客可以有多筆訂單
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_id",nullable=false)
    private Vendor vendor;

    
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

    @Column(name = "order_date", nullable = false)
    private Instant orderDate; 

    @Column(name ="shippingAddress",nullable=false)
    private String shippingAddress;

    @Enumerated(EnumType.STRING) 
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount; // 使用 BigDecimal 處理金額

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt; 

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt; 

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.orderDate == null) {
            this.orderDate = now;
        }
        if (this.status == null) {
            this.status = OrderStatus.PENDING; // 設定預設狀態
        }
    }

    

    // --- Helper methods for managing the bidirectional relationship ---
    public void addOrderItem(OrderItem item) {
        orderItems.add(item);
        item.setOrder(this);
    }

    public void removeOrderItem(OrderItem item) {
        orderItems.remove(item);
        item.setOrder(null);
    }



   
}
