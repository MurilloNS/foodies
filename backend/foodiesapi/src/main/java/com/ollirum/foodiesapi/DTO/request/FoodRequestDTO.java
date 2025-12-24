package com.ollirum.foodiesapi.DTO.request;

import java.math.BigDecimal;

public record FoodRequestDTO(String name, String description, BigDecimal price, String category) {
}