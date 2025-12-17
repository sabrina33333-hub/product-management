package com.example.productmanagement.service.impl;

import com.example.productmanagement.dto.request.CustomerRequest;
import com.example.productmanagement.dto.response.CategoryPurchaseStat;
import com.example.productmanagement.dto.response.CustomerProfile;
import com.example.productmanagement.dto.response.OrderSummary;
import com.example.productmanagement.entity.Customer;

import com.example.productmanagement.exception.ResourceNotFoundException;
import com.example.productmanagement.repository.CustomerRepository;
import com.example.productmanagement.repository.OrderRepository; // 【修正】為了統計，暫時保留
import com.example.productmanagement.service.CustomerService;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService {
    

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository; 

    //@Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, OrderRepository orderRepository) {
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        
    }

    @Transactional
    @Override
    public Customer createCustomer(CustomerRequest customerRequest) {
        
        customerRepository.findByEmail(customerRequest.email()).ifPresent(c -> {
            throw new IllegalStateException("此信箱: " + customerRequest.email()+"已被使用");
        });

        Customer customer = new Customer();
        
        customer.setCustomerName(customerRequest.customerName());
        customer.setEmail(customerRequest.email());
        customer.setPhone(customerRequest.phone());
        customer.setAddress(customerRequest.address());

        return customerRepository.save(customer);
    }

    @Override
    @Transactional(readOnly = true)
    public Customer getCustomerById(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到顧客: " + customerId+"ID"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Transactional
    @Override
    public Customer updateCustomer(Integer customerId, CustomerRequest customerRequest) {
        Customer existingCustomer = getCustomerById(customerId);

        // 更新資料
        
        existingCustomer.setCustomerName(customerRequest.customerName());
        existingCustomer.setPhone(customerRequest.phone());
        existingCustomer.setAddress(customerRequest.address());
        // Email 通常不允許直接修改，或者需要額外驗證流程，此處保持不變

        return customerRepository.save(existingCustomer);
    }

    @Transactional
    @Override
    public void deleteCustomer(Integer customerId) {
        
        Customer customer = getCustomerById(customerId);
        
        if (!customer.getOrders().isEmpty()) {
            throw new IllegalStateException("無法刪除顧客： " + customerId + " 因為尚有訂單存在.");
        }
        
        customerRepository.delete(customer);
    }
    
    @Override
    @Transactional(readOnly = true)
    public CustomerProfile getCustomerProfile(Integer customerId) {
        // 1. 獲取顧客核心實體
        Customer customer = customerRepository.findById(customerId)
        .orElseThrow(() -> new ResourceNotFoundException("找不到顧客 ID:"+customerId));
        // 2. 獲取該顧客的所有訂單摘要
        List<OrderSummary>orderSummaries =
        orderRepository.findByCustomerId(customerId).stream()
        .map(order -> new OrderSummary(
            order.getOrderId(),
            order.getOrderDate(),
            order.getTotalAmount(),
            order.getStatus()
                )).collect(Collectors.toList());
                
        List<CategoryPurchaseStat> categoryStatsList = orderRepository.findCategoryPurchaseStats(customerId);

        Map<String, Long> categoryStatisticsMap = categoryStatsList.stream()
                .collect(Collectors.toMap(
                        CategoryPurchaseStat::categoryName,
                        CategoryPurchaseStat::purchaseCount
                ));

         // 4. 組裝成 DTO (數據傳輸物件) 返回
        return new CustomerProfile(
                customer.getId(),
                customer.getCustomerName(),
                customer.getEmail(),
                customer.getPhone(),
                orderSummaries,
                categoryStatisticsMap
        );
    }





}