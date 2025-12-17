package com.example.productmanagement.repository;

import com.example.productmanagement.dto.response.ProductProfitability;
import com.example.productmanagement.dto.response.RawProductProfitability;
import com.example.productmanagement.dto.response.RawSummary;
import com.example.productmanagement.dto.response.TopProduct;
import com.example.productmanagement.entity.OrderItem;
import com.example.productmanagement.model.OrderStatus;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Integer> {

    List<OrderItem> findByOrder_OrderId(Integer orderId);

    @Query("SELECT new com.example.productmanagement.dto.response.TopProduct(oi.product.id, oi.product.name, SUM(oi.quantity)) " +
           "FROM OrderItem oi JOIN oi.order o " +
           "WHERE o.status = com.example.productmanagement.model.OrderStatus.COMPLETED " +
           "GROUP BY oi.product.id, oi.product.name " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<TopProduct> findTopSellingProducts(Pageable pageable);

    
    @Query("SELECT new com.example.productmanagement.dto.response.RawSummary(" +
           "COALESCE(SUM(p.price * oi.quantity), 0), " +
           "COALESCE(SUM(p.cost * oi.quantity), 0)) " +
           "FROM OrderItem oi JOIN oi.product p " +
           "WHERE oi.order.orderDate >= :start AND oi.order.orderDate <= :end " +
           "AND oi.order.status NOT IN ('CANCELLED', 'RETURNED')")
       Optional<RawSummary> findGlobalRawProfitSummary(@Param("start") LocalDate start, @Param("end") LocalDate end);
    
    
    @Query("SELECT new com.example.productmanagement.dto.response.RawProductProfitability(" +
            "p.name, " +
            "p.id, " +
            "COALESCE(SUM(oi.quantity),0L), " +
            "COALESCE(SUM(oi.purchasePrice * oi.quantity), 0.0), " +
            "COALESCE(SUM(p.cost * oi.quantity),0.0)) " +
            
            "FROM OrderItem oi JOIN oi.product p JOIN oi.order o " +
            "WHERE (:startDate IS NULL OR o.orderDate >= :startDate) " +
            "  AND (:endDate IS NULL OR o.orderDate <= :endDate) " +
            "  AND (:status IS NULL OR o.status = :status) " +
            "GROUP BY p.id, p.name " +
            "ORDER BY p.id DESC")
    List<RawProductProfitability> findProductProfitabilityWithFilters(
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("status") OrderStatus status);
                
                            /**
               * 單一商品 ID，查詢並計算每個商品的獲利能力。
               * - SUM(oi.quantity) as unitsSold: 計算總銷售件數
               * - SUM(oi.quantity * p.price) as totalRevenue: 計算總收入 (用當時的售價)
               * - SUM(oi.quantity * p.cost) as totalCost: 計算總成本 (用產品的單位成本)
               * - GROUP BY p.id, p.name: 按商品分組
               */
    

    @Query("SELECT new com.example.productmanagement.dto.response.ProductProfitability(" +
            "p.name, " +                      // 1. productName
            "p.id, " +                        // 2. productId  <-- 這就是我們補上的關鍵！
            "SUM(oi.quantity), " +            // 3. unitsSold
            "SUM(p.price * oi.quantity), " +  // 4. totalRevenue
            "SUM(p.cost * oi.quantity), " +   // 5. totalCost
            "SUM(p.price * oi.quantity) - SUM(p.cost * oi.quantity)) " + // 6. profit
            "FROM OrderItem oi JOIN oi.product p " +
            "GROUP BY p.id, p.name")
    List<ProductProfitability> findOverallProductProfitability();

}
