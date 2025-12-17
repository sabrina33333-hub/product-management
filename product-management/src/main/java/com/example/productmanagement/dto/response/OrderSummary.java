package com.example.productmanagement.dto.response;

import java.math.BigDecimal;
import java.time.Instant;


import com.example.productmanagement.model.OrderStatus;


public record OrderSummary(
    Integer orderId,
    Instant orderDate,
    BigDecimal totalAmount, // 對於金額，使用 BigDecimal 是最佳實踐，避免精度問題
    OrderStatus status
) {
}
