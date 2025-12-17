package com.example.productmanagement.service;
import com.example.productmanagement.dto.response.VendorDashboard;

/**
 * 儀表板服務，專門用於獲取和聚合儀表板所需的各種統計數據。
 */
public interface DashboardService {

    
    VendorDashboard getGlobalDashboardData();
}

