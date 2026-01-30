package com.example.productmanagement.service;

import com.example.productmanagement.entity.Product;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Excel 檔案處理服務介面
 * 
 * 定義了 Excel 檔案上傳、解析和驗證的核心功能
 * 
 * @author Your Name
 * @version 1.0
 * @since 2024-01-01
 */
public interface ExcelService {
    
    /**
     * 解析 Excel 檔案並轉換為 Product 列表
     * 
     * @param file 上傳的 Excel 檔案
     * @return 解析後的產品列表
     * @throws IOException 檔案讀取異常
     * @throws IllegalArgumentException 檔案格式不正確
     * @throws RuntimeException 資料解析失敗
     */
    List<Product> parseExcelFile(MultipartFile file) throws IOException;
    
    /**
     * 驗證檔案是否為有效的 Excel 格式
     * 
     * @param file 要驗證的檔案
     * @return true 如果是有效的 Excel 檔案，否則 false
     */
    boolean isValidExcelFile(MultipartFile file);
    
    /**
     * 獲取支援的 Excel 檔案格式列表
     * 
     * @return 支援的檔案格式陣列
     */
    String[] getSupportedFileTypes();
    
    /**
     * 獲取最大檔案大小限制（位元組）
     * 
     * @return 最大檔案大小
     */
    long getMaxFileSize();
    
    /**
     * 驗證 Excel 檔案的欄位結構
     * 
     * @param file 要驗證的檔案
     * @return true 如果欄位結構正確，否則 false
     * @throws IOException 檔案讀取異常
     */
    boolean validateExcelStructure(MultipartFile file) throws IOException;
}