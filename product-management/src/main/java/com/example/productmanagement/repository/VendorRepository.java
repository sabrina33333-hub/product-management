package com.example.productmanagement.repository;

import com.example.productmanagement.entity.User; // <<<< 【注意】請確保引入了 User 實體
import com.example.productmanagement.entity.Vendor;
import com.example.productmanagement.model.VendorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Integer> {

    
    Optional<Vendor> findByStoreName(String name);
    List<Vendor> findByStatus(VendorStatus status);
    boolean existsByVendorId(Long vendorId);
    //Optional<Vendor> findByUser(User user);

    //檢查店鋪名稱是否已存在
    boolean existsByStoreName(String storeName);
    
    //根據聯絡人查找供應商
    Optional<Vendor> findByContactPerson(String contactPerson);

    //查找活躍的供應商
    @Query("SELECT v FROM Vendor v WHERE v.status = 'ACTIVE'")
    List<Vendor> findActiveVendors();
    
    //根據店鋪名稱模糊查詢
    @Query("SELECT v FROM Vendor v WHERE v.storeName LIKE %:name%")
    List<Vendor> findByStoreNameContaining(@Param("name") String name);
    
    //檢查供應商是否有關聯的產品 (用於刪除驗證)
    @Query("SELECT COUNT(p) > 0 FROM Product p WHERE p.vendor.vendorId = :vendorId")
    boolean hasProducts(@Param("vendorId") Integer vendorId);

}
