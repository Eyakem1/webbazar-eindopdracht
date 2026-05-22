package com.webbazar.controller;

import com.webbazar.dto.CheckoutRequestDTO;
import com.webbazar.dto.OrderDTO;
import com.webbazar.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<OrderDTO> checkout(
            @Valid @RequestBody CheckoutRequestDTO dto,
            Authentication authentication
    ) {
        // E-mailadres uit de ingelogde gebruiker (komt uit de JWT)
        String email = authentication.getName();
        OrderDTO created = orderService.checkout(dto, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // Lijst met orders voor de  gebruiker, admin
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public List<OrderDTO> list(Authentication auth) {
        return orderService.listFor(auth);
    }
}
