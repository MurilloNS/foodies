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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

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

        long amountInCents = BigDecimal.valueOf(newOrder.getAmount()).multiply(BigDecimal.valueOf(100)).longValueExact();

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount(amountInCents)
                .setCurrency("brl")
                .putMetadata("orderId", newOrder.getId())
                .setReceiptEmail(newOrder.getEmail())
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        newOrder.setPaymentIntentId(paymentIntent.getId());
        newOrder.setOrderStatus("Preparing");
        newOrder =  orderRepository.save(newOrder);

        return OrderMapper.toResponseDTO(newOrder, paymentIntent.getClientSecret());
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

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getUserOrders() {
        List<Order> list = orderRepository.findByUserId(userService.findByUserId());
        return list.stream().map(OrderMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeOrder(String orderId) throws AccessDeniedException {
        String loggedInUserId = userService.findByUserId();
        Order order = orderRepository.findById(orderId)
                        .orElseThrow(() -> new UsernameNotFoundException("Order not found"));

        if (!order.getUserId().equals(loggedInUserId)) {
            throw new AccessDeniedException("You cannot delete this order");
        }

        orderRepository.deleteById(orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersOfAllUsers() {
        List<Order> list = orderRepository.findAll();
        return list.stream().map(OrderMapper::toResponseDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateOrderStatus(String orderId, String status) {
        Order entity = orderRepository.findById(orderId)
                .orElseThrow(() -> new UsernameNotFoundException("Order not found"));

        entity.setOrderStatus(status);
        orderRepository.save(entity);
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