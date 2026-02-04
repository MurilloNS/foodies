package com.ollirum.foodiesapi.controllers;

import com.ollirum.foodiesapi.DTO.request.OrderRequestDTO;
import com.ollirum.foodiesapi.DTO.response.OrderResponseDTO;
import com.ollirum.foodiesapi.services.OrderService;
import com.stripe.exception.StripeException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

@CrossOrigin("*")
@AllArgsConstructor
@RequestMapping("/api/orders")
@RestController
public class OrderController {
    public final OrderService orderService;

    @PostMapping("/create")
    public ResponseEntity<OrderResponseDTO> createOrderWithPayment(@RequestBody OrderRequestDTO orderRequestDTO) throws StripeException {
        OrderResponseDTO responseDTO = orderService.createOrder(orderRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PostMapping("/webhook/stripe")
    public ResponseEntity<String> handleStripeWebhook(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        orderService.processStripeWebhook(payload, sigHeader);
        return ResponseEntity.ok("received");
    }

    @GetMapping
    public ResponseEntity<List<OrderResponseDTO>> getOrders() {
        return ResponseEntity.ok(orderService.getUserOrders());
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable String orderId) throws AccessDeniedException {
        orderService.removeOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<OrderResponseDTO>> getOrdersOfAllUsers() {
        return ResponseEntity.ok(orderService.getOrdersOfAllUsers());
    }

    @PatchMapping("/status/{orderId}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable String orderId, @RequestParam String status) {
        orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok().build();
    }
}