package com.example.productmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.util.List;

// 導入您的 Service, Entity, DTO, Enum
import com.example.productmanagement.service.OrderService;
import com.example.productmanagement.entity.Order;

import com.example.productmanagement.dto.request.CreateOrderRequest;
import com.example.productmanagement.dto.request.UpdateOrderStatusRequest;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;
    

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 1. 建立一筆新訂單
    // POST /api/v1/orders
    @PostMapping
    public ResponseEntity<List<Order>> createOrder(@RequestBody CreateOrderRequest request) {
            

            List<Order> createdOrders = orderService.createOrder(request);
            
            return ResponseEntity.ok(createdOrders);
            }

    // 2. 根據 ID 查詢訂單
    // GET /api/v1/orders/1
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderByIdWithDetails(@PathVariable("id") Integer orderId) {
        Order order = orderService.getOrderByIdWithDetails(orderId)
        .orElseThrow(()-> new ResponseStatusException(
            HttpStatus.NOT_FOUND,"找不到ID為"+orderId+"的訂單"
        ));
        return ResponseEntity.ok(order);
    }

    // 3. 查詢特定用戶的所有訂單
    // GET /api/v1/orders/user/5
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getOrdersByUserId(@PathVariable Integer userId) {
        List<Order> orders = orderService.getOrdersByCustomerId(userId);
        return ResponseEntity.ok(orders);
    }

    // 4. 更新訂單狀態 (例如：出貨、取消)
    // PATCH /api/v1/orders/1/status
    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable("id") Integer orderId,
            @RequestBody UpdateOrderStatusRequest statusRequest) {
        Order updatedOrder = orderService.updateOrderStatus(orderId, statusRequest.newStatus());
        return ResponseEntity.ok(updatedOrder);
    }
}
