package com.example.productmanagement.dto.response;

import java.math.BigDecimal;

/**
 * 商品獲利能力分析 (不可變的 Record 版本)
 */
public record ProductProfitability(
    String productName,
    Integer productId,
    Long unitsSold,   //總銷售數量
    BigDecimal totalRevenue, //該商品總收入
    BigDecimal totalCost,    //該商品總成本
    BigDecimal profit, //該商品總利潤
    BigDecimal averageCost   //單一成本  
) {
    
}