package com.example.productmanagement.dto.response;

import java.util.List;
import java.util.Map;

public record CustomerProfile (
    Integer customerId,
    String customerName,
    String customerEmail,
    String phone,
    List<OrderSummary> pastOrders,
    Map<String, Long> categoryStatistics

){}
