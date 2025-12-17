package com.example.productmanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener; // 【修正】導入 Listener

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "CUSTOMERS")
@EntityListeners(AuditingEntityListener.class) // 【【【 實作修正 2：啟用審計功能 】】】
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Integer id;    

    @Column(name = "customer_name", nullable = false)
    private String customerName;

    @Column(name = "email", nullable = false, unique = true) // 顧客的 email 應該是唯一的
    private String email;

    @Column(name = "phone") // 電話通常不是必填項
    private String phone;

    @Column(name = "address") // 地址也可能在註冊時非必填
    private String address;

    @OneToMany(
        mappedBy = "customer", 
       
        // 絕不能因為顧客銷戶就刪除歷史訂單記錄。
        cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH}, 
        orphanRemoval = true, 
        fetch = FetchType.LAZY
    )
    private List<Order> orders = new ArrayList<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDate createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDate updatedAt;

    // 輔助方法來管理雙向關聯 
    public void addOrder(Order order) {
        this.orders.add(order);
        order.setCustomer(this);
    }

    public void removeOrder(Order order) {
        this.orders.remove(order);
        order.setCustomer(null);
    }
}