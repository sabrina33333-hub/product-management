package com.example.productmanagement.service;

import com.example.productmanagement.dto.request.ProductRequest;
import com.example.productmanagement.entity.Product;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    
    List<Product> findAll();
    List<Product> getProductsByCategoryId(Integer categoryId);
    //Optional<Product> findById(Integer id);
    
    void deleteById(Integer id);

    Optional<Product> findByIdForForm(Integer id); 
    Product createProduct(ProductRequest productRequest);
    Product updateProduct(Integer id, ProductRequest productRequest);
    

}
