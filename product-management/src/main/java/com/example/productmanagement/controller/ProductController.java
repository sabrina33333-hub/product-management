package com.example.productmanagement.controller;

import com.example.productmanagement.dto.request.ProductRequest;
import com.example.productmanagement.dto.response.ApiResponse;
import com.example.productmanagement.entity.Product;
import com.example.productmanagement.exception.ResourceNotFoundException;
import com.example.productmanagement.service.ProductService;
import com.example.productmanagement.service.impl.ProductServiceImpl;
import com.example.productmanagement.service.impl.ProductServiceImpl.UploadResult;

import jakarta.validation.Valid;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.ByteArrayOutputStream;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")

public class ProductController {

    @Autowired
    private ProductService productService;

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

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

    // ==================== Excel 檔案處理 ====================

    /**
     * 上傳 Excel 檔案並批次匯入產品
     * POST /api/products/upload
     */
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UploadResult>> uploadExcelFile(
            @RequestParam("file") MultipartFile file) {
        
        logger.info("接收到 Excel 檔案上傳請求，檔案名稱: {}", file.getOriginalFilename());
        
        try {
            // 基本檔案驗證
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("檔案不能為空"));
            }
            
            // 檢查檔案大小（5MB 限制）
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("檔案大小不能超過 5MB"));
            }
            
            // 檢查檔案類型
            String contentType = file.getContentType();
            if (contentType == null || 
                (!contentType.equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet") &&
                 !contentType.equals("application/vnd.ms-excel"))) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error("只接受 Excel 檔案格式 (.xlsx 或 .xls)"));
            }
            
            // 處理檔案上傳
            UploadResult result = productService.uploadExcelFile(file);
            
            if (result.hasErrors()) {
                logger.warn("Excel 檔案處理完成，但有 {} 個錯誤", result.getErrorCount());
                return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .body(ApiResponse.success(
                        String.format("部分成功：成功處理 %d 筆，失敗 %d 筆", 
                            result.getSuccessCount(), result.getErrorCount()), 
                        result));
            } else {
                logger.info("Excel 檔案處理完全成功，共處理 {} 筆資料", result.getSuccessCount());
                return ResponseEntity.ok(
                    ApiResponse.success(
                        String.format("成功匯入 %d 筆產品資料", result.getSuccessCount()), 
                        result));
            }
            
        } catch (IOException e) {
            logger.error("Excel 檔案讀取失敗: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error("Excel 檔案讀取失敗: " + e.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.error("檔案驗證失敗: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(e.getMessage()));
        } catch (Exception e) {
            logger.error("處理 Excel 檔案時發生未預期錯誤: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("處理檔案時發生錯誤: " + e.getMessage()));
        }
    }

    /**
     * 下載 Excel 範本檔案
     * GET /api/products/template
     */
    // @GetMapping("/template")
    // public ResponseEntity<ApiResponse<String>> downloadTemplate() {
    //     try {
    //         String templateInfo = "Excel 範本格式：name, price, cost, description, categoryName, stockQuantity, vendorName";
            
    //         return ResponseEntity.ok(
    //             ApiResponse.success("Excel 範本格式說明", templateInfo)
    //         );
    //     } catch (Exception e) {
    //         logger.error("獲取範本失敗: {}", e.getMessage());
    //         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
    //             .body(ApiResponse.error("獲取範本失敗: " + e.getMessage()));
    //     }
    // }

    /**
 * 下載 Excel 範本檔案
 * GET /api/products/template/download
 */
@GetMapping("/template/download")
public ResponseEntity<byte[]> downloadTemplate() {
    try {
        // 動態生成 Excel 範本
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("商品資料");
        
        // 創建標題行
        Row headerRow = sheet.createRow(0);
        String[] headers = {"name", "price", "cost", "description", "categoryName", "stockQuantity", "vendorName"};
        String[] headerNames = {"商品名稱", "售價", "成本", "商品描述", "類別名稱", "進貨數量", "供應商名稱"};
        
        // 設置標題
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
        }
        
        // 創建說明行
        Row descRow = sheet.createRow(1);
        for (int i = 0; i < headerNames.length; i++) {
            Cell cell = descRow.createCell(i);
            cell.setCellValue(headerNames[i] + (i < 5 ? "（必填）" : ""));
        }
        
        // 創建範例資料行
        Row exampleRow = sheet.createRow(2);
        Object[] exampleData = {"iPhone 15 Pro", 35000, 28000, "最新款蘋果手機", "手機", 50, "Apple Store"};
        for (int i = 0; i < exampleData.length; i++) {
            Cell cell = exampleRow.createCell(i);
            if (exampleData[i] instanceof Number) {
                cell.setCellValue(((Number) exampleData[i]).doubleValue());
            } else {
                cell.setCellValue(exampleData[i].toString());
            }
        }
        
        // 自動調整列寬
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // 轉換為 byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        byte[] excelBytes = outputStream.toByteArray();
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, 
                       "attachment; filename=\"products_template.xlsx\"")
                .header(HttpHeaders.CONTENT_TYPE, 
                       "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(excelBytes);
                
    } catch (Exception e) {
        logger.error("生成範本檔案失敗: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
}


}
