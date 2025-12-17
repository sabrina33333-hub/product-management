package com.example.productmanagement.dto.response;

import java.math.BigDecimal;


public record RawSummary(BigDecimal totalRevenue, BigDecimal totalCogs) {}
