package com.ollirum.foodiesapi.services;

import com.ollirum.foodiesapi.DTO.request.CartRequestDTO;
import com.ollirum.foodiesapi.DTO.response.CartResponseDTO;

public interface CartService {
    CartResponseDTO addToCart(CartRequestDTO request);
    CartResponseDTO getCart();
    void clearCart();
}