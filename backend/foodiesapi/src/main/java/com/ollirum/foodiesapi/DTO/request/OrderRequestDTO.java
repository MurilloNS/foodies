package com.ollirum.foodiesapi.DTO.request;

import com.ollirum.foodiesapi.entities.OrderItem;

import java.util.List;

public record OrderRequestDTO(List<OrderItem> orderedItems, String userAddress, double amount, String email, String phoneNumber, String orderStatus) {
}