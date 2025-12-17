package com.example.productmanagement.repository;
import com.example.productmanagement.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {

    
    @Query("SELECT DISTINCT c FROM Customer c " +
        "JOIN c.orders o "+
        "JOIN o.orderItems oi "+
        "JOIN oi.product p "+
        "WHERE p.vendor.id = :vendorId"
    )List<Customer>findCustomersByVendorId(@Param("vendorId")Integer vendorId);

    Optional<Customer> findByEmail(String email);
    
    List<Customer> findByCustomerNameContainingIgnoreCase(String customerName);


}

