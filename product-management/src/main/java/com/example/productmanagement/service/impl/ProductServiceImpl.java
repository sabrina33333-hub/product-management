package com.example.productmanagement.service.impl;

import com.example.productmanagement.dto.request.ProductRequest;

import com.example.productmanagement.entity.Category;
import com.example.productmanagement.entity.Product;
import com.example.productmanagement.entity.Vendor;
import com.example.productmanagement.repository.CategoryRepository;
import com.example.productmanagement.repository.ProductRepository;
import com.example.productmanagement.repository.VendorRepository;
import com.example.productmanagement.service.ExcelService;
import com.example.productmanagement.service.ProductService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor 
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final Integer LOW_STOCK_THRESHOLD = 10;
    private final CategoryRepository categoryRepository;
    private final VendorRepository vendorRepository;
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);
    
    private final ExcelService excelService;

    @Override
    @Transactional(readOnly = true) // 查詢操作，設為 readOnly 可優化效能
    public List<Product> findAll() {
        List<Product> products = productRepository.findAll();
        return products;
    }

    @Override
    public Optional<Product> findByIdForForm(Integer id) {
        return productRepository.findByIdWithAssociations(id);
    }

    @Override
    @Transactional 
    public Product createProduct(ProductRequest productRequest) {
       // 檢查商品名稱是否重複
       // 1. 根據 DTO 中的 ID，從資料庫查找完整的 Category 物件
        Category category = categoryRepository.findById(productRequest.categoryId())
                .orElseThrow(() -> new EntityNotFoundException("儲存商品失敗：找不到分類 ID " + productRequest.categoryId()));

        // 2. 根據 DTO 中的 ID，從資料庫查找完整的 Vendor 物件
        Vendor vendor = vendorRepository.findById(productRequest.vendorId())
                .orElseThrow(() -> new EntityNotFoundException("儲存商品失敗：找不到供應商 ID " + productRequest.vendorId()));


        // 將 DTO 轉換為 Entity
        Product newProduct = new Product();
        newProduct.setName(productRequest.name());
        newProduct.setDescription(productRequest.description());
        newProduct.setPrice(productRequest.price());
        newProduct.setCost(productRequest.cost());
        newProduct.setStockQuantity(productRequest.stockQuantity());
        newProduct.setImageUrl(productRequest.imageUrl());
        
    
        newProduct.setCategory(category);
        newProduct.setVendor(vendor);

        return productRepository.save(newProduct);
    }

    @Override
    @Transactional
    public Product updateProduct(Integer id, ProductRequest productRequest){
        // 1. 查找現有的商品，如果不存在則拋出例外
        Product existingProduct = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("嘗試更新不存在的商品，ID: {}", id);
                    return new ResponseStatusException(HttpStatus.NOT_FOUND, "找不到 ID 為 " + id + " 的商品");
                });

        // 2. 檢查更新的商品名稱是否與其他商品重複
        productRepository.findByName(productRequest.name()).ifPresent(p -> {
            if (!p.getId().equals(id)) { // 如果找到的商品不是當前正在更新的商品
                log.warn("嘗試將商品名稱更新為已存在的名稱: {}", productRequest.name());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "商品名稱 '" + productRequest.name() + "' 已被其他商品使用");
            }
        }); //先找商品名字 再核對ID

        // 3. 將 DTO 的資料更新到 Entity 中
        existingProduct.setName(productRequest.name());
        existingProduct.setDescription(productRequest.description());
        existingProduct.setPrice(productRequest.price());
        existingProduct.setStockQuantity(productRequest.stockQuantity());
       

        // 4. 儲存 (在 @Transactional 中，這一步有時可以省略，但明確寫出更清晰)
        return productRepository.save(existingProduct);
    }

    @Override
    @Transactional // 刪除操作，啟用交易管理
    public void softDeleteProduct(Integer id) {
       Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("找不到商品ID"));
        product.setActive(false); // 將狀態設為下架
        productRepository.save(product);
   }
 
    @Override
    @Transactional
    public void relistProduct(Integer id){
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("找不到商品ID"));
        product.setActive(true); // 將狀態設為下架
        productRepository.save(product);
    }
    
    

    

    @Override
    @Transactional(readOnly = true)
    public List<Product> getProductsByCategoryId(Integer categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }

    
    /**
     * 上傳並處理 Excel 檔案
     */
    public UploadResult uploadExcelFile(MultipartFile file) throws IOException {
        logger.info("開始處理 Excel 檔案上傳: {}", file.getOriginalFilename());
        
        // 基本驗證
        if (file.isEmpty()) {
            throw new IllegalArgumentException("檔案不能為空");
        }
        
        if (!excelService.isValidExcelFile(file)) {
            throw new IllegalArgumentException("只接受 Excel 檔案格式");
        }
        
        // 解析 Excel 檔案
        List<Product> products = excelService.parseExcelFile(file);
        
        if (products.isEmpty()) {
            throw new IllegalArgumentException("Excel 檔案中沒有有效的資料");
        }
        
        // 批次儲存產品
        List<String> errors = new ArrayList<>();
        List<Product> savedProducts = new ArrayList<>();
        int successCount = 0;
        
        for (Product product : products) {
            try {
                // 檢查產品名稱是否已存在
                if (productRepository.existsByName(product.getName())) {
                    errors.add("產品名稱 '" + product.getName() + "' 已存在");
                    continue;
                }
                
                Product savedProduct = productRepository.save(product);
                savedProducts.add(savedProduct);
                successCount++;
                
            } catch (Exception e) {
                logger.error("儲存產品失敗: {}", e.getMessage());
                errors.add("儲存產品 '" + product.getName() + "' 失敗: " + e.getMessage());
            }
        }
        
        logger.info("Excel 檔案處理完成，成功儲存 {} 筆記錄", successCount);
        
        return new UploadResult(successCount, errors, 
            savedProducts.subList(0, Math.min(5, savedProducts.size())));
    }
    
    // UploadResult 內部類別...
    public static class UploadResult {
        private int successCount;
        private List<String> errors;
        private List<Product> preview;
        
        public UploadResult(int successCount, List<String> errors, List<Product> preview) {
            this.successCount = successCount;
            this.errors = errors;
            this.preview = preview;
        }
        
        // Getters
        public int getSuccessCount() { return successCount; }
        public List<String> getErrors() { return errors; }
        public List<Product> getPreview() { return preview; }
        public boolean hasErrors() { return errors != null && !errors.isEmpty(); }
        
        // ✅ 新增這個方法來解決編譯錯誤
        public int getErrorCount() {
            return errors.size();
        }
        
        // ✅ 新增一些有用的方法
        public int getTotalProcessed() {
            return successCount + getErrorCount();
        }
    }

      
}
    

