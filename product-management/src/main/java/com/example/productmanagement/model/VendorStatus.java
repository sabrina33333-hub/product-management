package com.example.productmanagement.model;


public enum VendorStatus {
   
    
    ACTIVE("活躍"),
    SUSPENDED("暫停"),
    INACTIVE("停用");
    
    private final String displayName;
    
    VendorStatus(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    /**
     * 根據顯示名稱查找對應的狀態
     */
    public static VendorStatus fromDisplayName(String displayName) {
        for (VendorStatus status : values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        throw new IllegalArgumentException("未知的供應商狀態: " + displayName);
    }
}
