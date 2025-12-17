package com.example.productmanagement.controller.view;


import org.springframework.stereotype.Controller; 
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.productmanagement.service.DashboardService;
import lombok.RequiredArgsConstructor;

@Controller 
@RequestMapping("/dashboard") 
@RequiredArgsConstructor
public class DashboardViewController {

    private final DashboardService dashboardService;

    @GetMapping
    public String getDashboardPage(Model model) {
        
        
        // 獲取所有儀表板數據
        model.addAttribute("summary", dashboardService.getGlobalDashboardData());

        // 指定要渲染的片段和頁面標題
        model.addAttribute("view", "dashboard");
        model.addAttribute("pageTitle", "儀表板總覽");
        
        // 返回主佈局
        return "main";
    }
}
