package com.example.productmanagement.service.impl;

import com.example.productmanagement.dto.response.ProfitReport;
import com.example.productmanagement.dto.response.ProfitSummary;
import com.example.productmanagement.dto.response.RawProductProfitability;
import com.example.productmanagement.model.OrderStatus;
import com.example.productmanagement.dto.response.ProductProfitability;
import com.example.productmanagement.repository.OrderItemRepository;
import com.example.productmanagement.service.AnalyticsService;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;


@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {
    private final OrderItemRepository orderItemRepository;

    

    // @Override
    // public ProfitReport generateProfitReport(){
    //     List<ProductProfitability>profitabilityList = orderItemRepository.findOverallProductProfitability();

    //     BigDecimal totalRevenue = profitabilityList.stream().map(ProductProfitability::totalRevenue).reduce(BigDecimal.ZERO, BigDecimal::add);
    
    //     BigDecimal totalCost = profitabilityList.stream().map(ProductProfitability::totalCost).reduce(BigDecimal.ZERO, BigDecimal::add);
    
    //     BigDecimal netProfit = totalRevenue.subtract(totalCost);

    //     ProfitSummary summary = new ProfitSummary(totalRevenue, totalCost, netProfit);
        
        
    
    //     return new ProfitReport(summary, profitabilityList);
    // }

    
    
    @Override
    @Transactional(readOnly = true)
    public ProfitReport generateProfitReport(LocalDate startDate, LocalDate endDate, OrderStatus status) {

        // --- 開始轉換邏輯 ---
        Instant startInstant = null;
        if (startDate != null) {
            // 轉換為當天零點的 UTC 時間點
            startInstant = startDate.atStartOfDay().toInstant(ZoneOffset.UTC);
        }

        Instant endInstant = null;
        if (endDate != null) {
            // 轉換為當天最末尾時間點的 UTC 時間點
            endInstant = endDate.atTime(LocalTime.MAX).toInstant(ZoneOffset.UTC);
        }
        // --- 結束轉換邏輯 ---


        // 使用轉換後的 Instant 類型參數進行查詢
        List<RawProductProfitability> rawData = orderItemRepository.findProductProfitabilityWithFilters(
            startInstant, // <-- 使用轉換後的 startInstant
            endInstant,   // <-- 使用轉換後的 endInstant
            status
        );
        // 3. 計算總結數據
        // 使用 Stream API 將 List<RawProductProfitability> 轉換為 List<ProductProfitability>
        List<ProductProfitability>productProfitability = rawData.stream()
        .map(raw ->{
            BigDecimal totalCost = raw.totalCost();
            long unitsSold = raw.unitsSold();
            
            //平均成本
            BigDecimal averageCost;
            if(unitsSold >0){
                averageCost = totalCost.divide(BigDecimal.valueOf(unitsSold),2,RoundingMode.HALF_UP);
            }else{
                averageCost = BigDecimal.ZERO;
            }

            //計算利潤
            BigDecimal profit =raw.totalRevenue().subtract(raw.totalCost());

            //創建返回物件
            return new ProductProfitability(
                raw.productName(),
                raw.productId(),
                raw.unitsSold(),
                raw.totalRevenue(),
                raw.totalCost(),
                profit,
                averageCost
            );
        }).toList();

        BigDecimal totalRevenue = productProfitability.stream()
            .map(ProductProfitability::totalRevenue)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCost = productProfitability.stream()
            .map(ProductProfitability::totalCost)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal netProfit = totalRevenue.subtract(totalCost);

        ProfitSummary summary = new ProfitSummary(totalRevenue, totalCost, netProfit);

        return new ProfitReport(summary, productProfitability);
    }



}

