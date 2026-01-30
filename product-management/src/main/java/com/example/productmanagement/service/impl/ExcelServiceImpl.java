
package com.example.productmanagement.service.impl;

import com.example.productmanagement.entity.Product;
import com.example.productmanagement.entity.Vendor;
import com.example.productmanagement.entity.Category;
import com.example.productmanagement.service.ExcelService;
import com.example.productmanagement.repository.VendorRepository;
import com.example.productmanagement.repository.CategoryRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.Instant;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

/**
 * Excel 檔案處理服務實作類別
 * 支援 Vendor 和 Category 物件關聯，無需 User 關聯
 */
@Service
public class ExcelServiceImpl implements ExcelService {
    
    private static final Logger logger = LoggerFactory.getLogger(ExcelServiceImpl.class);
    
    // 注入需要的 Repository
    @Autowired
    private VendorRepository vendorRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    // 常數定義
    private static final String[] SUPPORTED_CONTENT_TYPES = {
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", // .xlsx
        "application/vnd.ms-excel" // .xls
    };
    
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
    
    // Excel 欄位標題定義
    private static final String[] EXPECTED_HEADERS = {
        "name",           // A欄: 產品名稱
        "price",          // B欄: 價格
        "cost",           // C欄: 成本
        "description",    // D欄: 描述
        "categoryName",   // E欄: 類別名稱
        "stockQuantity",  // F欄: 進貨數量
        "vendorName"      // G欄: 供應商名稱
    };
    
    /**
     * 解析 Excel 檔案並轉換為 Product 列表
     */
    @Override
    public List<Product> parseExcelFile(MultipartFile file) throws IOException {
        logger.info("開始解析 Excel 檔案: {}", file.getOriginalFilename());
        
        // 前置驗證
        validateFile(file);
        
        List<Product> products = new ArrayList<>();
        
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            
            // 跳過標題行 (第 1 行)
            if (rows.hasNext()) {
                Row headerRow = rows.next();
                validateHeaders(headerRow);
            }
            
            int rowNumber = 1; // 記錄當前行號
            int successCount = 0;
            
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                rowNumber++;
                
                // ==========================================
                // ✅ 新增邏輯：跳過說明行 (例如第 2 行)
                // 檢查 B 欄 (價格) 是否包含 "必填" 或 "售價" 等文字
                // ==========================================
                Cell priceCellCheck = currentRow.getCell(1);
                if (priceCellCheck != null && priceCellCheck.getCellType() == CellType.STRING) {
                    String cellText = priceCellCheck.getStringCellValue();
                    if (cellText.contains("必填") || cellText.contains("售價")) {
                        logger.info("跳過第 {} 行：檢測為說明行", rowNumber);
                        continue;
                    }
                }

                // 跳過空行
                if (isRowEmpty(currentRow)) {
                    logger.debug("跳過空行: {}", rowNumber);
                    continue;
                }
                
                try {
                    Product product = parseRowToProduct(currentRow, rowNumber);
                    if (product != null) {
                        products.add(product);
                        successCount++;
                    }
                } catch (Exception e) {
                    logger.error("第 {} 行資料解析失敗: {}", rowNumber, e.getMessage());
                    throw new RuntimeException("第 " + rowNumber + " 行資料解析失敗: " + e.getMessage());
                }
            }
            
            logger.info("Excel 檔案解析完成，成功解析 {} 筆資料", successCount);
            
        } catch (IOException e) {
            logger.error("讀取 Excel 檔案失敗: {}", e.getMessage());
            throw new IOException("讀取 Excel 檔案失敗: " + e.getMessage(), e);
        }
        
        return products;
    }
    
    /**
     * 將 Excel 行資料轉換為 Product 物件
     * 處理所有必填和選填欄位
     */
    private Product parseRowToProduct(Row row, int rowNumber) {
        Product product = new Product();
        
        try {
            // 欄位對應：A=name, B=price, C=cost, D=description, E=categoryName, F=stockQuantity, G=vendorName
            
            // 1. 產品名稱 (必填)
            Cell nameCell = row.getCell(0);
            if (nameCell == null || getCellValueAsString(nameCell).trim().isEmpty()) {
                throw new RuntimeException("產品名稱不能為空");
            }
            String productName = getCellValueAsString(nameCell).trim();
            product.setName(productName);
            
            // 2. 價格 (必填)
            Cell priceCell = row.getCell(1);
            if (priceCell == null) {
                throw new RuntimeException("價格不能為空");
            }
            
            double priceValue = getCellValueAsDouble(priceCell);
            if (priceValue < 0) {
                throw new RuntimeException("價格不能為負數");
            }
            product.setPrice(BigDecimal.valueOf(priceValue));
            
            // 3. 成本 (必填)
            Cell costCell = row.getCell(2);
            if (costCell == null) {
                throw new RuntimeException("成本不能為空");
            }
            
            double costValue = getCellValueAsDouble(costCell);
            if (costValue < 0) {
                throw new RuntimeException("成本不能為負數");
            }
            if (costValue > priceValue) {
                logger.warn("第 {} 行：成本 ({}) 高於售價 ({})", rowNumber, costValue, priceValue);
            }
            product.setCost(BigDecimal.valueOf(costValue));
            
            // 4. 描述 (選填)
            Cell descCell = row.getCell(3);
            if (descCell != null) {
                String description = getCellValueAsString(descCell).trim();
                if (!description.isEmpty()) {
                    product.setDescription(description);
                }
            }
            
            // 5. 類別 (必填) - 根據名稱查找或創建 Category 物件
            Cell categoryCell = row.getCell(4);
            if (categoryCell == null || getCellValueAsString(categoryCell).trim().isEmpty()) {
                throw new RuntimeException("類別名稱不能為空");
            }
            
            String categoryName = getCellValueAsString(categoryCell).trim();
            Category category = findOrCreateCategory(categoryName, rowNumber);
            product.setCategory(category);
            
            // 6. 庫存數量 (必填)
            Cell stockCell = row.getCell(5);
            if (stockCell == null) {
                throw new RuntimeException("庫存數量不能為空");
            }
            
            double stockValue = getCellValueAsDouble(stockCell);
            if (stockValue < 0) {
                throw new RuntimeException("庫存數量不能為負數");
            }
            product.setStockQuantity((int) Math.round(stockValue)); // 使用四捨五入
            
            // 7. 供應商 (必填) - 根據名稱查找或創建 Vendor 物件
            Cell vendorCell = row.getCell(6);
            if (vendorCell == null || getCellValueAsString(vendorCell).trim().isEmpty()) {
                throw new RuntimeException("供應商名稱不能為空");
            }
            
            String vendorName = getCellValueAsString(vendorCell).trim();
            Vendor vendor = findOrCreateVendor(vendorName, rowNumber);
            product.setVendor(vendor);
            
            product.setCreatedAt(Instant.now());
            product.setUpdatedAt(Instant.now());
            
        } catch (Exception e) {
            logger.error("解析第 {} 行時發生錯誤: {}", rowNumber, e.getMessage());
            throw new RuntimeException("第 " + rowNumber + " 行資料格式錯誤: " + e.getMessage());
        }
        
        return product;
    }
    
    /**
     * 根據類別名稱查找或創建 Category 物件
     */
    private Category findOrCreateCategory(String categoryName, int rowNumber) {
        try {
            // 先嘗試根據名稱查找現有的類別
            Optional<Category> existingCategory = categoryRepository.findByName(categoryName);
            
            if (existingCategory.isPresent()) {
                logger.debug("找到現有類別: {}", categoryName);
                return existingCategory.get();
            } else {
                // 如果不存在，創建新的類別
                logger.info("創建新類別: {}", categoryName);
                Category newCategory = new Category();
                newCategory.setName(categoryName);
                newCategory.setCreatedAt(Instant.now());
                newCategory.setUpdatedAt(Instant.now());

                // parentCategory 保持為 null (頂層類別)
                return categoryRepository.save(newCategory);
            }
        } catch (Exception e) {
            throw new RuntimeException("第 " + rowNumber + " 行：處理類別 '" + categoryName + "' 時發生錯誤: " + e.getMessage());
        }
    }
    
    /**
     * 根據供應商名稱查找或創建 Vendor 物件
     * 簡化版本：不需要 User 關聯
     */
    private Vendor findOrCreateVendor(String vendorName, int rowNumber) {
        try {
            // 先嘗試根據店鋪名稱查找現有的供應商
            Optional<Vendor> existingVendor = vendorRepository.findByStoreName(vendorName);
            
            if (existingVendor.isPresent()) {
                logger.debug("找到現有供應商: {}", vendorName);
                return existingVendor.get();
            } else {
                // 如果不存在，創建新的供應商
                logger.info("創建新供應商: {}", vendorName);
                Vendor newVendor = new Vendor();
                newVendor.setStoreName(vendorName);
                // 設定預設值
                newVendor.setStoreDescription("通過 Excel 匯入創建");
                // status 會在 @PrePersist 中自動設定為 ACTIVE
                // ✅ 關鍵修正：設定時間
                newVendor.setCreatedAt(Instant.now());
                newVendor.setUpdatedAt(Instant.now());
                return vendorRepository.save(newVendor);
            }
        } catch (Exception e) {
            throw new RuntimeException("第 " + rowNumber + " 行：處理供應商 '" + vendorName + "' 時發生錯誤: " + e.getMessage());
        }
    }
    
    // ==================== 介面實作方法 ====================
    
    @Override
    public boolean isValidExcelFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            logger.warn("檔案為空或不存在");
            return false;
        }
        
        String contentType = file.getContentType();
        boolean isValid = contentType != null && 
            Arrays.asList(SUPPORTED_CONTENT_TYPES).contains(contentType);
        
        if (!isValid) {
            logger.warn("不支援的檔案格式: {}", contentType);
        }
        
        return isValid;
    }
    
    @Override
    public String[] getSupportedFileTypes() {
        return SUPPORTED_CONTENT_TYPES.clone();
    }
    
    @Override
    public long getMaxFileSize() {
        return MAX_FILE_SIZE;
    }
    
    @Override
    public boolean validateExcelStructure(MultipartFile file) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            
            Sheet sheet = workbook.getSheetAt(0);
            Row headerRow = sheet.getRow(0);
            
            if (headerRow == null) {
                logger.warn("Excel 檔案缺少標題行");
                return false;
            }
            
            return validateHeaders(headerRow);
            
        } catch (Exception e) {
            logger.error("驗證 Excel 結構時發生錯誤: {}", e.getMessage());
            throw new IOException("驗證 Excel 結構失敗", e);
        }
    }
    
    // ==================== 私有輔助方法 ====================
    
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("檔案不能為空");
        }
        
        if (!isValidExcelFile(file)) {
            throw new IllegalArgumentException("只接受 Excel 檔案格式 (.xlsx 或 .xls)");
        }
        
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException(
                String.format("檔案大小超過限制 (%d MB)", MAX_FILE_SIZE / 1024 / 1024)
            );
        }
    }
    
    private boolean validateHeaders(Row headerRow) {
        if (headerRow == null) {
            return false;
        }
        
        for (int i = 0; i < EXPECTED_HEADERS.length; i++) {
            Cell cell = headerRow.getCell(i);
            String cellValue = getCellValueAsString(cell).toLowerCase().trim();
            String expectedHeader = EXPECTED_HEADERS[i].toLowerCase();
            
            if (!expectedHeader.equals(cellValue)) {
                logger.warn("標題行第 {} 欄期望 '{}' 但實際為 '{}'", 
                    i + 1, EXPECTED_HEADERS[i], cellValue);
                // 寬鬆模式：只記錄警告，不中斷處理
            }
        }
        
        return true;
    }
    
    private String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double numValue = cell.getNumericCellValue();
                    // 避免科學記號，整數顯示為整數
                    if (numValue == (long) numValue) {
                        return String.valueOf((long) numValue);
                    } else {
                        return String.valueOf(numValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    // 嘗試獲取公式的計算結果
                   return String.valueOf(cell.getNumericCellValue());
            } catch (IllegalStateException e) {
                try {
                    return cell.getStringCellValue();
                } catch (IllegalStateException e2) {
                    return cell.getCellFormula();
                }
            }
            case BLANK:
                return "";
            default:
                return "";
        }
    }
    
    private double getCellValueAsDouble(Cell cell) {
        if (cell == null) {
            return 0.0;
        }
        
        switch (cell.getCellType()) {
            case NUMERIC:
                return cell.getNumericCellValue();
            case STRING:
                String stringValue = cell.getStringCellValue().trim();
                if (stringValue.isEmpty()) {
                    return 0.0;
                }
                try {
                    // 處理可能的千分位符號
                    stringValue = stringValue.replace(",", "");
                    return Double.parseDouble(stringValue);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("無法將 '" + stringValue + "' 轉換為數字");
                }
            case BLANK:
                return 0.0;
            default:
                throw new RuntimeException("無法將儲存格類型 " + cell.getCellType() + " 轉換為數字");
        }
    }
    
    private boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }
        
        // 檢查前幾個重要欄位是否都為空
        for (int cellNum = 0; cellNum < 3; cellNum++) { // 檢查 name, price, cost
            Cell cell = row.getCell(cellNum);
            if (cell != null && !getCellValueAsString(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}
