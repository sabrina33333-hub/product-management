package com.example.productmanagement.dto.response;

import java.math.BigDecimal;

// 這是一個用於 JPA 查詢的內部數據傳輸物件
public record RawProductProfitability(
    String productName,
    Integer productId, 
    Long unitsSold, 
    BigDecimal totalRevenue, 
    BigDecimal totalCost
) {}
