package com.ollirum.foodiesapi.controllers;

import com.ollirum.foodiesapi.DTO.request.CartRequestDTO;
import com.ollirum.foodiesapi.DTO.response.CartResponseDTO;
import com.ollirum.foodiesapi.services.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("/api/cart")
public class CartController {
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartResponseDTO> addToCart(@RequestBody CartRequestDTO request) {
        CartResponseDTO response = cartService.addToCart(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<CartResponseDTO> getCart() {
        CartResponseDTO cart = cartService.getCart();
        return ResponseEntity.ok(cart);
    }

    @DeleteMapping
    public ResponseEntity<Void> clearCart() {
        cartService.clearCart();
        return ResponseEntity.noContent().build();
    }
}