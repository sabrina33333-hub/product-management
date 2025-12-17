package com.example.productmanagement.dto.response;



import java.util.List;

public record ProfitReport(
    ProfitSummary summary,
    List<ProductProfitability> productProfitability
) {}

