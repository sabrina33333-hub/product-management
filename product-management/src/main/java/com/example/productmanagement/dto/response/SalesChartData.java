package com.example.productmanagement.dto.response;

// 放置於 dto 套件下
import java.util.List;

public record SalesChartData(
    List<String> labels,
    List<Double> data 
){}

