package com.ollirum.foodiesapi.mappers;

import com.ollirum.foodiesapi.DTO.request.OrderRequestDTO;
import com.ollirum.foodiesapi.DTO.response.OrderResponseDTO;
import com.ollirum.foodiesapi.entities.Order;

public class OrderMapper {
    private OrderMapper() {}

    public static Order toEntity(OrderRequestDTO dto) {
        if (dto == null) return null;

        return Order.builder()
                .userAddress(dto.userAddress())
                .amount(dto.amount())
                .orderedItems(dto.orderedItems())
                .email(dto.email())
                .phoneNumber(dto.phoneNumber())
                .orderStatus(dto.orderStatus())
                .build();
    }

    public static OrderResponseDTO toResponseDTO(Order order) {
        if (order == null) return null;

        return OrderResponseDTO.builder()
                .id(order.getId())
                .amount(order.getAmount())
                .userAddress(order.getUserAddress())
                .userId(order.getUserId())
                .paymentIntentId(order.getPaymentIntentId())
                .paymentStatus(order.getPaymentStatus())
                .orderStatus(order.getOrderStatus())
                .paymentProvider(order.getPaymentProvider())
                .email(order.getEmail())
                .phoneNumber(order.getPhoneNumber())
                .orderedItems(order.getOrderedItems())
                .build();
    }

    public static OrderResponseDTO toResponseDTO(Order order, String clientSecret) {
        if (order == null) return null;

        return OrderResponseDTO.builder()
                .id(order.getId())
                .amount(order.getAmount())
                .userAddress(order.getUserAddress())
                .userId(order.getUserId())
                .paymentIntentId(order.getPaymentIntentId())
                .paymentStatus(order.getPaymentStatus())
                .orderStatus(order.getOrderStatus())
                .paymentProvider(order.getPaymentProvider())
                .email(order.getEmail())
                .phoneNumber(order.getPhoneNumber())
                .orderedItems(order.getOrderedItems())
                .clientSecret(clientSecret)
                .build();
    }
}