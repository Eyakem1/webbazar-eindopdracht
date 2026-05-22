package com.webbazar.security;

import com.webbazar.entity.Order;
import com.webbazar.entity.OrderItem;
import com.webbazar.entity.User;
import com.webbazar.repo.OrderItemRepository;
import com.webbazar.repo.OrderRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

class OwnershipGuardTest {

    @Test
    void canAccess_true_forOwner() {
        var orderRepo = Mockito.mock(OrderRepository.class);
        var itemRepo = Mockito.mock(OrderItemRepository.class);
        var guard = new OwnershipGuard(orderRepo, itemRepo);

        var user = User.builder().email("me@example.com").build();
        var order = Order.builder().id(1L).user(user).build();

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        Authentication auth = new TestingAuthenticationToken("me@example.com", "x");
        assertThat(guard.canAccessOrder(1L, auth)).isTrue();
    }

    @Test
    void canAccess_false_forOtherUser() {
        var orderRepo = Mockito.mock(OrderRepository.class);
        var itemRepo = Mockito.mock(OrderItemRepository.class);
        var guard = new OwnershipGuard(orderRepo, itemRepo);

        var owner = User.builder().email("owner@example.com").build();
        var order = Order.builder().id(1L).user(owner).build();

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        Authentication auth = new TestingAuthenticationToken("intruder@example.com", "x");
        assertThat(guard.canAccessOrder(1L, auth)).isFalse();
    }

    @Test
    void canAccessOrderItem_true_forOwner() {
        var orderRepo = Mockito.mock(OrderRepository.class);
        var itemRepo = Mockito.mock(OrderItemRepository.class);
        var guard = new OwnershipGuard(orderRepo, itemRepo);

        var user = User.builder().email("me@example.com").build();
        var order = Order.builder().id(10L).user(user).build();
        var item = new OrderItem();
        item.setOrder(order);

        when(itemRepo.findById(5L)).thenReturn(Optional.of(item));

        Authentication auth = new TestingAuthenticationToken("me@example.com", "x");
        assertThat(guard.canAccessOrderItem(5L, auth)).isTrue();
    }

    @Test
    void canAccessOrderItem_false_forOtherUser() {
        var orderRepo = Mockito.mock(OrderRepository.class);
        var itemRepo = Mockito.mock(OrderItemRepository.class);
        var guard = new OwnershipGuard(orderRepo, itemRepo);

        var owner = User.builder().email("owner@example.com").build();
        var order = Order.builder().id(10L).user(owner).build();
        var item = new OrderItem();
        item.setOrder(order);

        when(itemRepo.findById(6L)).thenReturn(Optional.of(item));

        Authentication auth = new TestingAuthenticationToken("someone@example.com", "x");
        assertThat(guard.canAccessOrderItem(6L, auth)).isFalse();
    }
}
