package com.example.productmanagement.service;
import com.example.productmanagement.dto.request.CreateOrderRequest;
import com.example.productmanagement.entity.Order;
import com.example.productmanagement.model.OrderStatus;

import java.util.List;
import java.util.Optional;


public interface OrderService {

    /**
     * 【恢復】創建一筆新訂單。
     * 由 OrderItemService 協同完成。
     */
    List<Order> createOrder(CreateOrderRequest request);

    /**
     * 【恢復】根據訂單 ID 查詢單一訂單 (老闆視角)。
     */
    Optional<Order> getOrderByIdWithDetails(Integer orderId);

    /**
     * 【恢復】查詢特定顧客的所有訂單 (老闆視角)。
     */
    List<Order> getOrdersByCustomerId(Integer customerId);

    /**
     * 【恢復並簡化】更新訂單狀態 (老闆視角)。
     */
    Order updateOrderStatus(Integer orderId, OrderStatus newStatus);

    /**
     * 【新增】獲取所有訂單 (老闆視角)。
     */
    List<Order> getAllOrders();

    /**
     * 【新增】根據狀態查詢訂單 (老闆視角)。
     */
    List<Order> getOrdersByStatus(OrderStatus status);

    List<Order> getOrdersByVendorIdAndStatus(Integer vendorId, OrderStatus pending);
    
    //Order findById(Integer orderId);

}