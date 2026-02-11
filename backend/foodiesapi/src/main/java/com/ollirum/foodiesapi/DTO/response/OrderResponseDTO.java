package com.ollirum.foodiesapi.DTO.response;

import com.ollirum.foodiesapi.entities.OrderItem;
import lombok.Builder;

import java.util.List;

@Builder
public record OrderResponseDTO(String id, String userId, String userAddress, String phoneNumber, String email, double amount, String paymentStatus, String paymentProvider, String paymentIntentId, String clientSecret, String orderStatus, List<OrderItem> orderedItems) {
}