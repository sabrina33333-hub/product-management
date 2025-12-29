package com.example.productmanagement.controller;

import com.example.productmanagement.dto.request.ProductRequest;
import com.example.productmanagement.entity.Product;
import com.example.productmanagement.exception.ResourceNotFoundException;
import com.example.productmanagement.service.ProductService;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")

public class ProductController {

    @Autowired
    private ProductService productService;

    

    // --- R: Read (查詢) ---
    // Part 1: 查詢所有產品
    // HTTP Method: GET
    // URL: /api/products
    @GetMapping
    public List<Product> getAllProducts() {
        return productService.findAll();
    }

    // Part 2: 根據 ID 查詢單一產品
    // HTTP Method: GET
    // URL: /api/products/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer id) {
        Optional<Product> productOptional = productService.findByIdForForm(id);
        
        
        return productOptional
                .map(product -> ResponseEntity.ok(product)) // 如果找到，回傳 200 OK 和產品資料
                .orElse(ResponseEntity.notFound().build()); // 如果找不到，回傳 404 Not Found
    }

    // 3. 根據分類 ID 查詢商品
    // GET /api/v1/products?categoryId=5
    @GetMapping(params = "categoryId")
    public ResponseEntity<List<Product>> getProductsByCategory(@RequestParam Integer categoryId) {
        List<Product> products = productService.getProductsByCategoryId(categoryId);
        return ResponseEntity.ok(products);
    }

    // 4. 新增一個商品
    // POST /api/v1/products
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@Valid @RequestBody ProductRequest productRequest) {
        Product createdProduct = productService.createProduct(productRequest);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED); // 回傳 201 Created
    }

    // 5.更新一個商品--- U: Update ---
    // HTTP Method: PUT
    // URL: /api/products/{id}
     @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Integer id,
            @RequestBody ProductRequest productRequest) {
        
        try {
            // 將所有業務邏輯委託給 Service 層的 updateProduct 方法
            // 現在傳遞的參數類型 (Integer, ProductRequest) 與 Service 介面匹配了
            Product updatedProduct = productService.updateProduct(id, productRequest);
            
            // Service 成功執行，回傳 200 OK 和更新後的資料
            return ResponseEntity.ok(updatedProduct);
        } catch (ResourceNotFoundException ex) {
            // 如果 Service 在查找時拋出例外，表示找不到資源
            // Controller 捕捉這個例外，並回傳 404 Not Found
            return ResponseEntity.notFound().build();
        }
}
    

    // --- D: Delete (刪除) ---
    // HTTP Method: DELETE
    // URL: /api/products/{id}
    // @DeleteMapping("/{id}")
    // public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
    //     // 先檢查是否存在，這樣才能給出更精確的回應
    //     if (productService.findByIdForForm(id).isPresent()) {
    //         productService.deleteById(id);
    //         return ResponseEntity.noContent().build(); // 回傳 204 No Content，表示成功刪除
    //     } else {
    //         return ResponseEntity.notFound().build(); // 如果找不到，回傳 404 Not Found
    //     }
    // }
}
