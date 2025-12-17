package com.example.productmanagement.model;

public enum OrderStatus {
    PENDING("待處理"),        // 待處理
    PROCESSING("處理中"),     // 處理中
    SHIPPED("已出貨"),        // 已出貨
    DELIVERED("已送達"),      // 已送達
    CANCELLED("已取消"),      // 已取消
    RETURNED("已退貨"),       // 已退貨
    COMPLETED("已完成");

    private final String displayName; // <-- 這就是勝利的關鍵！從 aDisplayName 變為 displayName！

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { // <-- Getter 也隨之改變！
        return displayName;
}
}
