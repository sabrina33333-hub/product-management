package com.example.productmanagement.dto.request;


import com.example.productmanagement.model.OrderStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor; // <-- 導入這個
import lombok.AllArgsConstructor; // <-- 導入這個

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor  // <-- 【關鍵】Lombok 會幫您產生一個無參數的建構子
@AllArgsConstructor // <-- 這個會產生一個包含所有參數的建構子，方便測試
public class CreateOrderRequest {

    private String customerName;
    private String customerEmail;
    private LocalDate orderDate;
    private String shippingAddress;
    private OrderStatus status;

    private List<OrderItemRequest>items = new ArrayList<>();

}
