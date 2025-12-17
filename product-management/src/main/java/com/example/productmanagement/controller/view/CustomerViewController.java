package com.example.productmanagement.controller.view;

import com.example.productmanagement.dto.response.CustomerProfile;
import com.example.productmanagement.service.CustomerService;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.productmanagement.entity.Customer; 



@Controller
@RequestMapping("/customers")
@RequiredArgsConstructor // 使用 Lombok 進行依賴注入
public class CustomerViewController {

    private final CustomerService customerService;

    /**
     * 視圖方法：顯示顧客列表頁面
     */
    @GetMapping
    public String showCustomerList(Model model) {
               
        List<Customer> customers = customerService.getAllCustomers();
        model.addAttribute("customers", customers);
        
        // 【改造#2】設定頁面標題和要嵌入的視圖片段
        model.addAttribute("pageTitle", "顧客列表");
        model.addAttribute("view", "customers/index"); // 指向新的片段路徑

        // 【改造#3】返回主佈局
        return "main"; 
    }

    /**
     * 視圖方法：顯示單一顧客的詳細分析頁面
     */
    @GetMapping("/{customerId}")
    public String showCustomerDetails(@PathVariable Integer customerId, Model model) {
        

        CustomerProfile profile = customerService.getCustomerProfile(customerId);

        model.addAttribute("profile", profile);
        
        // 【改造#2】設定頁面標題和要嵌入的視圖片段
        model.addAttribute("pageTitle", "顧客詳情分析");
        model.addAttribute("view", "customers/show"); // 指向新的片段路徑

        // 【改造#3】返回主佈局
        return "main";
    }
}