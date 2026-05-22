package com.webbazar.controller;

import com.webbazar.dto.OrderDTO;
import com.webbazar.entity.Order;
import com.webbazar.entity.OrderStatus;
import com.webbazar.exception.ConflictException;
import com.webbazar.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
public class OrderCommandController {

    private final OrderService orderService;

    public OrderCommandController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{id}/pay")
    @PreAuthorize("@ownershipGuard.canAccessOrder(#id, authentication) or hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> pay(@PathVariable Long id) {
        Order o = orderService.findByIdOrThrow(id);
        if (o.getStatus() != OrderStatus.PENDING) {
            throw new ConflictException("ORDER_NOT_PAYABLE", "Order is niet meer te betalen.");
        }
        return ResponseEntity.ok(orderService.toDto(orderService.markPaid(o)));
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("@ownershipGuard.canAccessOrder(#id, authentication) or hasRole('ADMIN')")
    public ResponseEntity<OrderDTO> cancel(@PathVariable Long id) {
        Order o = orderService.findByIdOrThrow(id);
        if (o.getStatus() != OrderStatus.PENDING) {
            throw new ConflictException("ORDER_NOT_CANCELLABLE", "Order kan niet meer geannuleerd worden.");
        }
        return ResponseEntity.ok(orderService.toDto(orderService.markCancelled(o)));
    }
}
