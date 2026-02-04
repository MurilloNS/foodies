package com.ollirum.foodiesapi.services;

import com.ollirum.foodiesapi.DTO.request.OrderRequestDTO;
import com.ollirum.foodiesapi.DTO.response.OrderResponseDTO;
import com.stripe.exception.StripeException;

import java.nio.file.AccessDeniedException;
import java.util.List;

public interface OrderService {
    OrderResponseDTO createOrder(OrderRequestDTO requestDTO) throws StripeException;
    void processStripeWebhook(String payload, String sigHeader);
    void handlePaymentSucceeded(String paymentIntentId);
    void handlePaymentFailed(String paymentIntentId);
    List<OrderResponseDTO> getUserOrders();
    void removeOrder(String orderId) throws AccessDeniedException;
    List<OrderResponseDTO> getOrdersOfAllUsers();
    void updateOrderStatus(String orderId, String status);
}