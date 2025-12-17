package com.example.productmanagement.dto.response;

// 放置於 dto 套件下
public record TopProduct(
    Integer productId,
    String productName,
    Long totalQuantitySold) {
} 

    
