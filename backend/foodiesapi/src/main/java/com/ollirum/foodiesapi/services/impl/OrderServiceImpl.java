package com.ollirum.foodiesapi.services.impl;

import com.ollirum.foodiesapi.DTO.request.OrderRequestDTO;
import com.ollirum.foodiesapi.DTO.response.OrderResponseDTO;
import com.ollirum.foodiesapi.entities.Order;
import com.ollirum.foodiesapi.mappers.OrderMapper;
import com.ollirum.foodiesapi.repositories.OrderRepository;
import com.ollirum.foodiesapi.services.OrderService;
import com.ollirum.foodiesapi.services.UserService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@AllArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderResponseDTO createOrder(OrderRequestDTO requestDTO) throws StripeException {
        Order newOrder = OrderMapper.toEntity(requestDTO);
        newOrder.setPaymentStatus("PENDING");
        newOrder.setPaymentProvider("STRIPE");
        String loggedInUserId = userService.findByUserId();
        newOrder.setUserId(loggedInUserId);
        newOrder = orderRepository.save(newOrder);

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (newOrder.getAmount() * 100))
                .setCurrency("brl")
                .putMetadata("orderId", newOrder.getId())
                .setReceiptEmail(newOrder.getEmail())
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        newOrder.setPaymentIntentId(paymentIntent.getId());
        newOrder.setPaymentStatus(paymentIntent.getStatus());
        newOrder =  orderRepository.save(newOrder);

        return OrderMapper.toResponseDTO(newOrder);
    }
}