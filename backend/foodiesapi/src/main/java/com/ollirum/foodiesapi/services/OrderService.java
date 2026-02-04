package com.ollirum.foodiesapi.services;

import com.ollirum.foodiesapi.DTO.request.OrderRequestDTO;
import com.ollirum.foodiesapi.DTO.response.OrderResponseDTO;
import com.stripe.exception.StripeException;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO requestDTO) throws StripeException;
    void processStripeWebhook(String payload, String sigHeader);
    void handlePaymentSucceeded(String paymentIntentId);
    void handlePaymentFailed(String paymentIntentId);
}