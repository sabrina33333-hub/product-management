package com.example.productmanagement.dto.request;


import jakarta.validation.constraints.*;
import java.math.BigDecimal;


public record ProductRequest(
    @NotNull(message = "產品ID不得為空")
    Integer id,
    
    @NotBlank(message = "商品名稱不可為空")
    String name,

    @NotNull(message = "商品名稱不可為空")
    Integer categoryId, 

    
    String description,
    
    @NotNull(message = "價格不可為空")
    @Positive(message = "價格必須是正數")
    BigDecimal price,

    @NotNull(message = "成本價不可為空")
    @Positive(message = "成本價必須是正數")
    BigDecimal cost,
    
    @NotNull(message = "庫存不可為空")
    @Min(value = 0, message = "庫存不可為負數")
    Integer stockQuantity,

    String imageUrl,

    @NotNull(message = "供應商 ID 不可為空")
    Integer vendorId
) {}

