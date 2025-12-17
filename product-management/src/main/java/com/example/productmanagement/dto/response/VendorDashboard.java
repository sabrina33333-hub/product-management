package com.example.productmanagement.dto.response;


import com.example.productmanagement.entity.Product;
import java.util.List;

public record VendorDashboard(
    DashboardStats monthlyStats,
    long pendingOrderCount,
    List<TopProduct> topSellingProducts,
    List<Product> lowStockProducts,
    SalesChartData salesChartData



) {} 
    
  