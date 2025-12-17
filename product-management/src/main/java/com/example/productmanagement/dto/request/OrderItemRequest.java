package com.example.productmanagement.dto.request;
import java.util.ArrayList;
import java.util.List;

import com.example.productmanagement.entity.OrderItem;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor // <-- 【核心】Lombok 會自動產生一個無參數的建構子
public class OrderItemRequest {

    private Integer productId;

    private int quantity; 
    private List<OrderItem> items = new ArrayList<>();}

   



