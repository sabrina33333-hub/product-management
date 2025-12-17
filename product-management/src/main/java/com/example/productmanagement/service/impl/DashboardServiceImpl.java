package com.example.productmanagement.service.impl;


import java.util.Map;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId; 

import com.example.productmanagement.dto.response.DashboardStats;
import com.example.productmanagement.dto.response.SalesChartData;
import com.example.productmanagement.dto.response.TopProduct;
import com.example.productmanagement.dto.response.VendorDashboard;
import com.example.productmanagement.entity.Product;
import com.example.productmanagement.repository.OrderItemRepository;
import com.example.productmanagement.repository.OrderRepository;
import com.example.productmanagement.repository.ProductRepository;
import com.example.productmanagement.service.DashboardService;
import com.example.productmanagement.service.OrderService;
import com.example.productmanagement.model.OrderStatus; 
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    private static final int LOW_STOCK_THRESHOLD = 10; // 低庫存門檻值
    private static final int TOP_PRODUCTS_LIMIT = 5;   // 熱銷商品排行顯示數量
    private static final int SALES_CHART_DAYS = 7; // 顯示過去 7 天的銷售趨勢
    private static final ZoneId SERVER_ZONE_ID = ZoneId.of("Asia/Taipei");
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;
    

    //@Autowired
    public DashboardServiceImpl(OrderRepository orderRepository, 
        OrderItemRepository orderItemRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productRepository = productRepository;
      
    }

    @Override
    public VendorDashboard getGlobalDashboardData() {
        // 1. 獲取本月訂單數、總金額
        YearMonth currentMonth = YearMonth.now(SERVER_ZONE_ID);
        
        Instant startOfMonth = currentMonth.atDay(1).atStartOfDay(SERVER_ZONE_ID).toInstant(); 
        
        Instant startOfNextMonth = currentMonth.plusMonths(1).atDay(1).atStartOfDay(SERVER_ZONE_ID).toInstant();


        DashboardStats monthlyStats = orderRepository.findGlobalStatsForPeriod
        (startOfMonth,startOfNextMonth).orElse(new DashboardStats(0,BigDecimal.ZERO,0L));

        

        // 2. 獲取待處理訂單數
        long pendingOrderCount = orderRepository.countByStatus(OrderStatus.PENDING); 
        // 3. 獲取熱銷商品排行 (Top 5)
        List<TopProduct> topProducts = orderItemRepository.findTopSellingProducts(PageRequest.of(0, TOP_PRODUCTS_LIMIT));

        // 4. 獲取低庫存商品警告
        List<Product> lowStockProducts = productRepository.findByStockQuantityLessThan(LOW_STOCK_THRESHOLD);
        // 5. 獲取銷售趨勢圖數據
        SalesChartData salesChartData = getGenerateSalesChartData();

        // 6. 組裝成最終的 DTO
        return new VendorDashboard(
        monthlyStats,
        pendingOrderCount,
        topProducts,
        lowStockProducts,
        salesChartData
        );
        
        
    }
     public SalesChartData getGenerateSalesChartData() {
        // 1. 計算本月的第一天和下個月的第一天
        YearMonth currentMonth = YearMonth.now(SERVER_ZONE_ID);
        LocalDate startDate = currentMonth.atDay(1);
       
        int daysInMonth = currentMonth.lengthOfMonth();

        Instant startOfMonthInstant = startDate.atStartOfDay(SERVER_ZONE_ID).toInstant();
        Instant startOfNextMonthInstant = currentMonth.plusMonths(1).atDay(1).atStartOfDay(SERVER_ZONE_ID).toInstant();

        // 2. 調用新的 Repository 方法，查詢整個月份的數據
        List<Object[]> results = orderRepository.findDailyRevenueForPeriod(startOfMonthInstant, startOfNextMonthInstant);

        // 3. 將查詢結果放入 Map 中，方便查找
        Map<LocalDate, Double> dailyDataMap = results.stream()
                .collect(Collectors.toMap(
                        row -> ((java.sql.Date) row[0]).toLocalDate(),
                        row -> row[1] instanceof BigDecimal ? ((BigDecimal)row[1]).doubleValue():(Double)row[1]
                ));

        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        
        // 4. 循環遍歷本月的每一天，生成標籤和數據
        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate date = currentMonth.atDay(i);
            labels.add(date.format(DateTimeFormatter.ofPattern("MM-dd")));
            data.add(dailyDataMap.getOrDefault(date, 0.0));
        }

        return new SalesChartData(labels, data);
}

}

