package com.example.productmanagement.dto.response;



import java.math.BigDecimal;


public record ProfitSummary(
    BigDecimal totalRevenue, //總收入
    BigDecimal totalCost,    //總成本
    BigDecimal netProfit    //淨利潤（收入－成本）
    
) {
   
}