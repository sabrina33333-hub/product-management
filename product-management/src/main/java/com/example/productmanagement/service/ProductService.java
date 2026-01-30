package com.example.productmanagement.service;

import com.example.productmanagement.dto.request.ProductRequest;
import com.example.productmanagement.entity.Product;
import com.example.productmanagement.service.impl.ProductServiceImpl.UploadResult;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

public interface ProductService {

    
    List<Product> findAll();
    List<Product> getProductsByCategoryId(Integer categoryId);
    //Optional<Product> findById(Integer id);
    
    void softDeleteProduct(Integer id);
    void relistProduct(Integer ProductId);

    Optional<Product> findByIdForForm(Integer id); 
    Product createProduct(ProductRequest productRequest);
    Product updateProduct(Integer id, ProductRequest productRequest);
    UploadResult uploadExcelFile(MultipartFile file)throws IOException;
   
}
