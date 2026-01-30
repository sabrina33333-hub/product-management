package com.example.productmanagement.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 統一的 API 回應格式
 * 用於包裝所有 Controller 的回應資料
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    
    /**
     * 回應狀態碼
     * 200: 成功, 400: 客戶端錯誤, 500: 伺服器錯誤
     */
    private int code;
    
    /**
     * 回應訊息
     * 描述操作結果的文字訊息
     */
    private String message;
    
    /**
     * 實際的資料內容
     * 泛型設計，可以包含任何類型的資料
     */
    private T data;
    
    /**
     * 回應時間戳
     * 記錄 API 回應的時間
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    
    /**
     * 是否成功
     * true: 操作成功, false: 操作失敗
     */
    private boolean success;

    // ==================== 靜態工廠方法 ====================

    /**
     * 創建成功回應
     * @param message 成功訊息
     * @param data 回應資料
     * @return 成功的 ApiResponse
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
            .code(200)
            .message(message)
            .data(data)
            .timestamp(LocalDateTime.now())
            .success(true)
            .build();
    }

    /**
     * 創建成功回應（無資料）
     * @param message 成功訊息
     * @return 成功的 ApiResponse
     */
    public static <T> ApiResponse<T> success(String message) {
        return success(message, null);
    }

    /**
     * 創建錯誤回應
     * @param message 錯誤訊息
     * @return 錯誤的 ApiResponse
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
            .code(400)
            .message(message)
            .data(null)
            .timestamp(LocalDateTime.now())
            .success(false)
            .build();
    }

    /**
     * 創建自訂錯誤回應
     * @param code 錯誤代碼
     * @param message 錯誤訊息
     * @return 錯誤的 ApiResponse
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
            .code(code)
            .message(message)
            .data(null)
            .timestamp(LocalDateTime.now())
            .success(false)
            .build();
    }

    /**
     * 創建伺服器錯誤回應
     * @param message 錯誤訊息
     * @return 伺服器錯誤的 ApiResponse
     */
    public static <T> ApiResponse<T> serverError(String message) {
        return error(500, message);
    }
}