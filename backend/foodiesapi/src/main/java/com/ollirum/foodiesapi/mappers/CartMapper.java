package com.ollirum.foodiesapi.mappers;

import com.ollirum.foodiesapi.DTO.response.CartResponseDTO;
import com.ollirum.foodiesapi.entities.Cart;

public class CartMapper {
    private CartMapper() {}

    public static CartResponseDTO toResponseDTO(Cart cart) {
        if (cart == null) return null;

        return CartResponseDTO.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(cart.getItems())
                .build();
    }
}