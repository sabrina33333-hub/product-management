package com.example.productmanagement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.example.productmanagement.model.VendorStatus;

// --- Lombok 註解 ---
@Getter
@Setter
@NoArgsConstructor // 【最佳實踐】自動產生 JPA 需要的無參數建構子
// --- JPA 註解 ---
@Entity
@Table(name = "VENDORS")
public class Vendor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "vendor_id")
    private Integer vendorId; // 習慣上常命名為 id

    /**
     * 【已修正 & 最佳實踐】與 User 實體建立一對一關聯。
     * fetch = FetchType.LAZY: 延遲載入，提升效能。
     * optional = false: 表示 Vendor 必須關聯一個 User，在資料庫層面 user_id 不可為 null。
     * @JoinColumn: 指定外鍵欄位。
     */
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user; // 假設您有一個 User 實體

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "store_description", length = 500) // 建議加上長度限制
    private String storeDescription;

    @Column(name = "contact_person")
    private String contactPerson;

    @Column(name = "contact_phone")
    private String contactPhone;

    @Column(name = "address")
    private String address;

    /**
     * 【最佳實踐】使用 Enum 來表示狀態，並以字串形式存入資料庫。
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private VendorStatus status;

    /**
     * 【最佳實踐】一個廠商可以有多個商品。
     * mappedBy = "vendor": 表示這個關聯的擁有方在 Product 實體的 "vendor" 屬性中定義。
     * cascade = CascadeType.ALL: 當廠商被刪除時，其下的所有商品也一併刪除。
     * orphanRemoval = true: 從 products 列表中移除的商品將會從資料庫中刪除。
     */
    @OneToMany(mappedBy = "vendor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    /**
     * 【最佳實踐】一個廠商可以有多筆訂單。
     * 通常不建議連鎖刪除訂單，所以這裡不加 CascadeType.REMOVE。
     */
    @OneToMany(mappedBy = "vendor", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @Column(name = "created_at", updatable = false, nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    /**
     * 【最佳實踐】使用 JPA 生命週期回呼，在實體被儲存前自動設定時間。
     */
    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = VendorStatus.ACTIVE; // 設定預設狀態
        }
    }

    /**
     * 【最佳實踐】在實體被更新前自動更新時間。
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }

    // --- Helper methods for bidirectional relationship ---
    public void addProduct(Product product) {
        products.add(product);
        product.setVendor(this);
    }

    public void removeProduct(Product product) {
        products.remove(product);
        product.setVendor(null);
    }
}