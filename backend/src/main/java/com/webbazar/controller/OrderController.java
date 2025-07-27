package com.webbazar.controller;

import com.webbazar.dto.OrderRequest;
import com.webbazar.model.Order;
import com.webbazar.model.Product;
import com.webbazar.model.User;
import com.webbazar.repository.OrderRepository;
import com.webbazar.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ResponseEntity<List<Order>> getMyOrders(@AuthenticationPrincipal User user) {
        List<Order> orders = orderRepository.findByUser(user);
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<?> placeOrder(@RequestBody OrderRequest request, @AuthenticationPrincipal User user) {
        if (request.getProductIds() == null || request.getProductIds().isEmpty()) {
            return ResponseEntity.badRequest().body("Geen producten geselecteerd voor bestelling.");
        }

        if (!"huur".equalsIgnoreCase(request.getType()) && !"koop".equalsIgnoreCase(request.getType())) {
            return ResponseEntity.badRequest().body("Bestelling moet 'huur' of 'koop' zijn.");
        }

        List<Product> products = productRepository.findAllById(request.getProductIds());
        if (products.isEmpty()) {
            return ResponseEntity.badRequest().body("Geen geldige producten gevonden.");
        }

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setType(request.getType());
        order.setProducts(products);

        double total = products.stream()
                .mapToDouble(p -> "huur".equalsIgnoreCase(request.getType()) ? p.getRentalPrice() : p.getPrice())
                .sum();
        order.setTotalAmount(total);

        Order saved = orderRepository.save(order);
        return ResponseEntity.status(201).body(saved);
    }
}
