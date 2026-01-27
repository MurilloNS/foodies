package com.ollirum.foodiesapi.controllers;

import com.ollirum.foodiesapi.DTO.request.CartRequestDTO;
import com.ollirum.foodiesapi.DTO.response.CartResponseDTO;
import com.ollirum.foodiesapi.services.CartService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
}