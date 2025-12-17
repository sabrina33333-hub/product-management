package com.example.productmanagement.dto.response;

import java.math.BigDecimal;

public record DashboardStats(
    long orderCount,
    BigDecimal totalRevenue,
    long totalItems) {

        public static DashboardStats empty() {
        return new DashboardStats(0L, BigDecimal.ZERO, 0L);
    }
}
   

