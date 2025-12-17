package com.example.productmanagement.dto.response;

import java.math.BigDecimal;

public record CategoryPurchaseStat (
    String categoryName,
    BigDecimal totalQuantity,
    BigDecimal totalAmount,
    Long purchaseCount
){}
