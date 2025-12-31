package com.ollirum.foodiesapi.mappers;

import com.ollirum.foodiesapi.DTO.request.FoodRequestDTO;
import com.ollirum.foodiesapi.DTO.response.FoodResponseDTO;
import com.ollirum.foodiesapi.entities.Food;

public class FoodMapper {
    private FoodMapper() {}

    public static Food toEntity(FoodRequestDTO dto) {
        if (dto == null) return null;

        return Food.builder()
                .name(dto.name())
                .description(dto.description())
                .price(dto.price())
                .category(dto.category())
                .build();
    }

    public static FoodResponseDTO toResponseDTO(Food food) {
        if (food == null) return null;

        return FoodResponseDTO.builder()
                .id(food.getId())
                .name(food.getName())
                .description(food.getDescription())
                .category(food.getCategory())
                .price(food.getPrice())
                .imageUrl(food.getImageUrl())
                .build();
    }
}