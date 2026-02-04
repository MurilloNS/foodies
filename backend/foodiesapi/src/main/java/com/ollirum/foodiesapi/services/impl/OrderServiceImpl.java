package com.ollirum.foodiesapi.services.impl;

import com.ollirum.foodiesapi.DTO.request.OrderRequestDTO;
import com.ollirum.foodiesapi.DTO.response.OrderResponseDTO;
import com.ollirum.foodiesapi.entities.Order;
import com.ollirum.foodiesapi.mappers.OrderMapper;
import com.ollirum.foodiesapi.repositories.CartRepository;
import com.ollirum.foodiesapi.repositories.OrderRepository;
import com.ollirum.foodiesapi.services.OrderService;
import com.ollirum.foodiesapi.services.UserService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final UserService userService;
    private final CartRepository cartRepository;
    
    @Value("${stripe.webhook-secret}")
    private String webhookSecret;

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

    @Override
    @Transactional
    public void processStripeWebhook(String payload, String sigHeader) {
        Event event;

        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            System.err.println("Invalid Stripe signature");
            return;
        }

        switch (event.getType()) {
            case "payment_intent.succeeded" -> handlePaymentIntentSucceeded(event);
            case "payment_intent.payment_failed" -> handlePaymentIntentFailed(event);
            default -> {}
        }
    }

    @Override
    @Transactional
    public void handlePaymentSucceeded(String paymentIntentId) {
        orderRepository.findByPaymentIntentId(paymentIntentId)
                .ifPresent(order -> {
                    order.setPaymentStatus("PAID");
                    orderRepository.save(order);
                    cartRepository.deleteByUserId(order.getUserId());
                });
    }

    @Override
    @Transactional
    public void handlePaymentFailed(String paymentIntentId) {
        orderRepository.findByPaymentIntentId(paymentIntentId)
                .ifPresent(order -> {
                    order.setPaymentStatus("FAILED");
                    orderRepository.save(order);
                });
    }

    private void handlePaymentIntentSucceeded(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject().orElse(null);

        if (paymentIntent == null) {
            System.err.println("PaymentIntent null in succeeded event");
            return;
        }

        handlePaymentSucceeded(paymentIntent.getId());
    }

    private void handlePaymentIntentFailed(Event event) {
        PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                .getObject().orElse(null);

        if (paymentIntent == null) {
            System.err.println("PaymentIntent null in failed event");
            return;
        }

        handlePaymentFailed(paymentIntent.getId());
    }
}