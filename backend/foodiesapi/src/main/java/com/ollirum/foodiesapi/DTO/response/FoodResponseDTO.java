package com.ollirum.foodiesapi.DTO.response;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record FoodResponseDTO(String name, String description, String imageUrl, BigDecimal price, String category) {
}