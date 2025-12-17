package com.example.productmanagement.service;

import com.example.productmanagement.dto.request.CustomerRequest;
import com.example.productmanagement.dto.response.CustomerProfile;
import com.example.productmanagement.entity.Customer;

import java.util.List;

/**
 * 客戶服務介面，定義客戶資料相關的業務操作。
 */
public interface CustomerService {

    Customer createCustomer(CustomerRequest customerRequest);

    Customer getCustomerById(Integer customerId);

    List<Customer> getAllCustomers();

    Customer updateCustomer(Integer customerId, CustomerRequest customerRequest);

    void deleteCustomer(Integer customerId);

    CustomerProfile getCustomerProfile(Integer customerId);
}
 
    

