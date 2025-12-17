package com.example.productmanagement.controller.view;

import com.example.productmanagement.dto.response.ProfitReport;
import com.example.productmanagement.model.OrderStatus;
import com.example.productmanagement.service.AnalyticsService;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;




@Controller
@RequestMapping("/analytics")
public class AnalyticsViewController {
    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsViewController(AnalyticsService analyticsService){
        this.analyticsService = analyticsService;
    }

    // 在 AnalyticsViewController.java 中

@GetMapping("/report")
public String showSalesReport(
    // 告知 Spring 如何將 "yyyy-MM-dd" 字串轉為 LocalDate
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
    @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
    // 告知 Spring 將字串轉為 OrderStatus 枚舉
    @RequestParam(required = false) OrderStatus status,
    Model model) {

    


    // 命令 AnalyticsService 根據篩選條件生成報告
    ProfitReport report = analyticsService.generateProfitReport(startDate, endDate, status);

    // 將報告數據和【當前的篩選條件】一起放回 Model，以便前端“記住”用戶的選擇
    model.addAttribute("summary", report.summary());
    model.addAttribute("productProfitability", report.productProfitability());


    model.addAttribute("currentStartDate", startDate);
    model.addAttribute("currentEndDate", endDate);
    model.addAttribute("currentStatus", status != null ? status.name():"");

    // 設定頁面標題和視圖
    model.addAttribute("pageTitle", "收益分析報告");
    model.addAttribute("view", "analytics/report");

    return "main";
}

    
}
