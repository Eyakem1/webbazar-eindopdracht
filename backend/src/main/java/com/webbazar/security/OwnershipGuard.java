package com.webbazar.security;

import com.webbazar.entity.Order;
import com.webbazar.repo.OrderRepository;
import com.webbazar.repo.OrderItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("ownershipGuard")
@RequiredArgsConstructor
public class OwnershipGuard {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    // Controle op eigenaar van order
    public boolean canAccessOrder(Long orderId, Authentication auth) {
        return orderRepository.findById(orderId)
                .map(Order::getUser)
                .map(u -> u.getEmail().equalsIgnoreCase(auth.getName()))
                .orElse(false);
    }

    // Controle op eigenaar van orderitem
    public boolean canAccessOrderItem(Long orderItemId, Authentication auth) {
        return orderItemRepository.findById(orderItemId)
                .map(oi -> oi.getOrder().getUser().getEmail())
                .map(email -> email.equalsIgnoreCase(auth.getName()))
                .orElse(false);
    }
}
