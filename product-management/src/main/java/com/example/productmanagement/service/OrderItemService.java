package com.example.productmanagement.service;

import com.example.productmanagement.dto.request.OrderItemRequest;
import com.example.productmanagement.entity.Order;
import com.example.productmanagement.entity.OrderItem;
import java.util.List;


/**
 * 訂單項目服務介面，主要由 OrderService 內部使用。
 * 負責處理訂單項目的建立和查詢。
 */
public interface OrderItemService {

    /**
     * 為一個訂單建立單一的訂單項目。
     * 這個方法會處理庫存檢查和扣減。
     *
     * @param order      該項目所屬的訂單實體。
     * @param itemDto    包含 productId 和 quantity 的請求 DTO。
     * @return 建立的訂單項目實體。
     */
    OrderItem createOrderItem(Order order, OrderItemRequest itemDto);

    /**
     * 根據訂單 ID 查詢所有關聯的訂單項目。
     *
     * @param orderId 訂單的 ID。
     * @return 該訂單的所有訂單項目列表。
     */
    List<OrderItem> getOrderItemsByOrderId(Integer orderId);

    
}

