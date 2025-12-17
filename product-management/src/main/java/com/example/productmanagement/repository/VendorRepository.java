package com.example.productmanagement.repository;

import com.example.productmanagement.entity.User; // <<<< 【注意】請確保引入了 User 實體
import com.example.productmanagement.entity.Vendor;
import com.example.productmanagement.model.VendorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Integer> {

    // --- 您原有的方法 (保持不變) ---
    Optional<Vendor> findByStoreName(String name);
    List<Vendor> findByStatus(VendorStatus status);
    boolean existsByVendorId(Long vendorId);


    // --- 【【【 請在這裡加入這個關鍵方法！ 】】】 ---
    /**
     * 根據關聯的 User 物件來查找 Vendor。
     * Spring Data JPA 非常聰明，它會自動解析這個方法名稱，
     * 並生成類似 "SELECT v FROM Vendor v WHERE v.user = ?1" 的查詢。
     *
     * @param user 要查找的 User 實體
     * @return 一個可能包含對應 Vendor 的 Optional 物件
     */
    Optional<Vendor> findByUser(User user);
    // --- 【【【 新增結束 】】】 ---

}
