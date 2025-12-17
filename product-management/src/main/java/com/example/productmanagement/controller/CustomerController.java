package com.example.productmanagement.controller;

import com.example.productmanagement.dto.response.CustomerProfile;
import com.example.productmanagement.entity.Customer;
import com.example.productmanagement.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/customers") 
public class CustomerController {

    private final CustomerService customerService;

    
    @Autowired
    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * API 端點: 顯示所有顧客列表 (index)
     * HTTP 方法: GET
     * URL: /api/v1/customers
     */
    @GetMapping
    public ResponseEntity<List<Customer>> getAllCustomers() {
        
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(customers);
    }

    /**
     * API 端點: 顯示單一顧客的詳細資料 (show)
     * HTTP 方法: GET
     * URL: /api/v1/customers/{customerId}
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<CustomerProfile> getCustomerProfile(@PathVariable Integer customerId) {
        
        CustomerProfile profile = customerService.getCustomerProfile(customerId);
        return ResponseEntity.ok(profile);
    }
    
  
}