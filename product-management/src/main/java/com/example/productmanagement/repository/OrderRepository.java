package com.example.productmanagement.repository;

import com.example.productmanagement.dto.response.CategoryPurchaseStat;
import com.example.productmanagement.dto.response.DashboardStats;

import com.example.productmanagement.entity.Order;
// 【【【 核心修正 #1：引用正規軍 entity.OrderStatus！ 】】】
import com.example.productmanagement.model.OrderStatus;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {


       @Query("SELECT o FROM Order o JOIN FETCH o.customer JOIN FETCH o.orderItems oi JOIN FETCH oi.product WHERE o.id = :orderId")
       Optional<Order> findByIdWithDetails(@Param("orderId") Integer orderId);


         // 使用 JOIN FETCH 一次性獲取訂單和關聯的顧客
       @Query("SELECT o FROM Order o JOIN FETCH o.customer ORDER BY o.orderId ASC")
       List<Order> findAllWithCustomer();

       // 為了篩選功能，也建立一個 JOIN FETCH 版本
       @Query("SELECT o FROM Order o JOIN FETCH o.customer WHERE o.status = :status ORDER BY o.orderId ASC")
       List<Order> findByStatusWithCustomer(@Param("status")OrderStatus status);
       // --- 儀表板與統計查詢 ---

      @Query("SELECT new com.example.productmanagement.dto.response.DashboardStats(" +
           // 參數 1: 訂單總數
            "CAST(COUNT(DISTINCT o.id) AS int), " +

            // 參數 2: 總收入 (注意 COALESCE 的括號和結尾的逗號)
            "COALESCE(SUM(CASE WHEN o.status = com.example.productmanagement.model.OrderStatus.COMPLETED THEN oi.quantity * oi.purchasePrice ELSE 0 END), 0.0), " +

            // 參數 3: 售出商品總數
            "COALESCE(SUM(CASE WHEN o.status = com.example.productmanagement.model.OrderStatus.COMPLETED THEN oi.quantity ELSE 0 END), 0L)) " +

           "FROM Order o JOIN o.orderItems oi " +
           "WHERE o.orderDate >= :start AND o.orderDate < :end")
       Optional<DashboardStats> findGlobalStatsForPeriod(@Param("start") Instant start, @Param("end") Instant end);

       @Query("SELECT DISTINCT o FROM Order o " +
           "JOIN o.orderItems oi " +
           "JOIN oi.product p " +
           "WHERE p.vendor.id = :vendorId AND o.status = :status")
       List<Order> findOrdersByVendorIdAndStatus(@Param("vendorId") Integer vendorId, @Param("status") OrderStatus status);


       @Query("SELECT FUNCTION('DATE', o.orderDate), SUM(oi.quantity * oi.purchasePrice) " +
           "FROM Order o JOIN o.orderItems oi " +
           "WHERE o.orderDate >= :start AND o.orderDate < :end AND o.status = com.example.productmanagement.model.OrderStatus.COMPLETED " +
           "GROUP BY FUNCTION('DATE', o.orderDate) " +
           "ORDER BY FUNCTION('DATE', o.orderDate) ASC")
        List<Object[]> findDailyRevenueForPeriod(@Param("start") Instant start, @Param("end") Instant end);

       // --- 顧客相關查詢 ---

      
       List<Order> findByCustomerId( Integer customerId);

      @Query("SELECT DISTINCT o FROM Order o " +
           "JOIN o.orderItems oi " +
           "JOIN oi.product p " +
           "WHERE o.customer.id = :customerId AND p.vendor.id = :vendorId " +
           "ORDER BY o.orderDate DESC")
       List<Order> findOrdersByCustomerAndVendor(@Param("customerId") Integer customerId,@Param("vendorId") Integer vendorId);



       @Query("SELECT new com.example.productmanagement.dto.response.CategoryPurchaseStat(" +
           "p.category.name, " +
           "CAST(SUM(oi.quantity) AS java.math.BigDecimal), " + // 顯式轉換為 BigDecimal
           "SUM(p.price * oi.quantity), " +
           "COUNT(o.id)) " +
           "FROM Order o " +
           "JOIN o.orderItems oi " +
           "JOIN oi.product p " +
           "WHERE o.customer.id = :customerId AND o.status = com.example.productmanagement.model.OrderStatus.COMPLETED " +
           "GROUP BY p.category.name")
    List<CategoryPurchaseStat> findCategoryPurchaseStats(@Param("customerId") Integer customerId);
       
       List<Order> findByStatus(OrderStatus status);
      
       long countByStatus(OrderStatus status);

}