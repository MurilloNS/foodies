package com.ollirum.foodiesapi.services.impl;

import com.ollirum.foodiesapi.DTO.request.CartRequestDTO;
import com.ollirum.foodiesapi.DTO.response.CartResponseDTO;
import com.ollirum.foodiesapi.entities.Cart;
import com.ollirum.foodiesapi.mappers.CartMapper;
import com.ollirum.foodiesapi.repositories.CartRepository;
import com.ollirum.foodiesapi.services.CartService;
import com.ollirum.foodiesapi.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@AllArgsConstructor
@Service
public class CartServiceImpl implements CartService {
    private final CartRepository cartRepository;
    private final UserService userService;

    @Override
    public CartResponseDTO addToCart(CartRequestDTO request) {
        String loggedInUserId = userService.findByUserId();
        Optional<Cart> cartOptional = cartRepository.findByUserId(loggedInUserId);
        Cart cart = cartOptional.orElseGet(() -> new Cart(loggedInUserId, new HashMap<>()));
        Map<String, Integer> cartItems = cart.getItems();
        cartItems.put(request.foodId(), cartItems.getOrDefault(request.foodId(), 0) + 1);
        cart.setItems(cartItems);
        cart = cartRepository.save(cart);

        return CartMapper.toResponseDTO(cart);
    }
}