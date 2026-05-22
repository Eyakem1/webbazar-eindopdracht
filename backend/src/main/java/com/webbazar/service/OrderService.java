package com.webbazar.service;

import com.webbazar.dto.CheckoutRequestDTO;
import com.webbazar.dto.OrderDTO;
import com.webbazar.entity.Order;
import org.springframework.security.core.Authentication;

import java.util.List;


public interface OrderService {

    // Nieuwe order aanmaken
    OrderDTO checkout(CheckoutRequestDTO request, String email);

    // Orders ophalen
    List<OrderDTO> listFor(Authentication auth);

    // Order ophalen als DTO
    OrderDTO getDtoById(Long id);

    // Order ophalen of exception gooien
    Order findByIdOrThrow(Long orderId);

    // Order als betaald markeren
    Order markPaid(Order order);

    // Order annuleren
    Order markCancelled(Order order);

    // Entity → DTO helper.
    OrderDTO toDto(Order order);
}
