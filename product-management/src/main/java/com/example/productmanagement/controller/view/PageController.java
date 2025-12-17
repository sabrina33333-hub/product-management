package com.example.productmanagement.controller.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;



/**
 * 這位是我們帝國的「接待員」控制器。
 * 它的職責是處理那些不需要複雜數據加載的、簡單的頁面訪問請求。
 */
@Controller
public class PageController {

    @GetMapping("/login")
    public String showLoginPage() {
        
        return "login";
    }

  
    @GetMapping("/user-registration")
    public String showRegistrationPage() {
        
        return "user-registration";
    }

    
}