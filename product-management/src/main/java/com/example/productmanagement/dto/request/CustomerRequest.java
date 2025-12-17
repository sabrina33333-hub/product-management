package com.example.productmanagement.dto.request;


public record CustomerRequest (

    String email,
    String customerName,
    String phone,
    String address
){}

    