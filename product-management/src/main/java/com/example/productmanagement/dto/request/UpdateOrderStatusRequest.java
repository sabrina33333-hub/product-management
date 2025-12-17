package com.example.productmanagement.dto.request;

import com.example.productmanagement.model.OrderStatus;

public record UpdateOrderStatusRequest (
    OrderStatus newStatus
){}
   
