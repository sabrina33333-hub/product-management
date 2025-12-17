package com.example.productmanagement.service;
import java.time.LocalDate;

import com.example.productmanagement.dto.response.ProfitReport;
import com.example.productmanagement.model.OrderStatus;

public interface AnalyticsService {
    
    ProfitReport generateProfitReport(LocalDate startDate, LocalDate endDate, OrderStatus status);
    //ProfitReport generateProfitReport();
} 
