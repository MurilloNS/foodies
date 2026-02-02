package com.ollirum.foodiesapi.DTO.response;

import lombok.Builder;

@Builder
public record OrderResponseDTO(String id, String userId, String userAddress, String phoneNumber, String email, double amount, String paymentStatus, String paymentProvider, String paymentIntentId, String orderStatus) {
}