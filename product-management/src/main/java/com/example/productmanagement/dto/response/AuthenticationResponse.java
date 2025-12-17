package com.example.productmanagement.dto.response;

import lombok.Builder;


@Builder
public record AuthenticationResponse(String token,Integer userId,Integer vendorId) {
}
