package com.webbazar.service;

import com.webbazar.dto.CheckoutRequestDTO;
import com.webbazar.dto.OrderItemDTO;
import com.webbazar.dto.RentalDTO;
import com.webbazar.entity.*;
import com.webbazar.repo.OrderRepository;
import com.webbazar.repo.ProductRepository;
import com.webbazar.repo.UserRepository;
import com.webbazar.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock OrderRepository orderRepo;
    @Mock ProductRepository productRepo;
    @Mock UserRepository userRepo;

    @InjectMocks OrderServiceImpl orderService;

    private Order pending;

    @BeforeEach
    void setup() {
        pending = Order.builder()
                .id(1L)
                .status(OrderStatus.PENDING)
                .build();
    }

    @Test
    void checkout_withBuyItem_usesProductPriceAndFallbackRootType() {
        User user = user(1L, "buyer@example.com");
        Product product = product(10L, "Book", "15.00", "3.00");

        CheckoutRequestDTO request = checkoutRequest("BUY", item(10L, null, 2, null));

        when(userRepo.findByEmail("buyer@example.com")).thenReturn(Optional.of(user));
        when(productRepo.findById(10L)).thenReturn(Optional.of(product));
        when(orderRepo.save(any())).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setId(99L);
            order.getItems().get(0).setId(100L);
            return order;
        });

        var dto = orderService.checkout(request, "buyer@example.com");

        assertThat(dto.getId()).isEqualTo(99L);
        assertThat(dto.getTotal()).isEqualByComparingTo("30.00");
        assertThat(dto.getItems().get(0).getType()).isEqualTo("BUY");
        assertThat(dto.getItems().get(0).getQuantity()).isEqualTo(2);
        assertThat(dto.getItems().get(0).getRental()).isNull();
    }

    @Test
    void checkout_withRentItem_usesRentalPriceAndProvidedRentalPeriod() {
        User user = user(1L, "renter@example.com");
        Product product = product(10L, "Book", "15.00", "3.50");

        RentalDTO rental = new RentalDTO();
        rental.setStartDate(Instant.parse("2025-01-01T00:00:00Z"));
        rental.setEndDate(Instant.parse("2025-01-08T00:00:00Z"));

        CheckoutRequestDTO request = checkoutRequest("BUY", item(10L, "RENT", 0, rental));

        when(userRepo.findByEmail("renter@example.com")).thenReturn(Optional.of(user));
        when(productRepo.findById(10L)).thenReturn(Optional.of(product));
        when(orderRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var dto = orderService.checkout(request, "renter@example.com");

        assertThat(dto.getTotal()).isEqualByComparingTo("3.50");
        assertThat(dto.getItems().get(0).getType()).isEqualTo("RENT");
        assertThat(dto.getItems().get(0).getQuantity()).isEqualTo(1);
        assertThat(dto.getItems().get(0).getRental().getStartDate()).isEqualTo(rental.getStartDate());
        assertThat(dto.getItems().get(0).getRental().getEndDate()).isEqualTo(rental.getEndDate());
    }

    @Test
    void checkout_withRentItemWithoutRentalPeriod_usesDefaultPeriod() {
        User user = user(1L, "renter@example.com");
        Product product = product(10L, "Book", "15.00", "3.50");

        CheckoutRequestDTO request = checkoutRequest("RENT", item(10L, "", 1, null));

        when(userRepo.findByEmail("renter@example.com")).thenReturn(Optional.of(user));
        when(productRepo.findById(10L)).thenReturn(Optional.of(product));
        when(orderRepo.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var dto = orderService.checkout(request, "renter@example.com");

        assertThat(dto.getItems().get(0).getRental()).isNotNull();
        assertThat(dto.getItems().get(0).getRental().getEndDate())
                .isAfter(dto.getItems().get(0).getRental().getStartDate());
    }

    @Test
    void checkout_withMissingEmail_throwsIllegalArgument() {
        assertThatThrownBy(() ->
                orderService.checkout(checkoutRequest("BUY", item(1L, "BUY", 1, null)), " ")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email ontbreekt");
    }

    @Test
    void checkout_withUnknownUser_throwsIllegalArgument() {
        when(userRepo.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                orderService.checkout(checkoutRequest("BUY", item(1L, "BUY", 1, null)), "missing@example.com")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Gebruiker niet gevonden");
    }

    @Test
    void checkout_withMissingProduct_throwsIllegalArgument() {
        when(userRepo.findByEmail("buyer@example.com"))
                .thenReturn(Optional.of(user(1L, "buyer@example.com")));
        when(productRepo.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                orderService.checkout(checkoutRequest("BUY", item(404L, "BUY", 1, null)), "buyer@example.com")
        )
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Product niet gevonden");
    }

    @Test
    void listFor_withAdminAuth_returnsAllOrders() {
        Authentication auth = mock(Authentication.class);

        doReturn(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .when(auth).getAuthorities();

        when(orderRepo.findAllByOrderByIdDesc())
                .thenReturn(List.of(orderWithItem(1L, user(1L, "a@example.com"))));

        var result = orderService.listFor(auth);

        assertThat(result).hasSize(1);
        verify(orderRepo).findAllByOrderByIdDesc();
    }

    @Test
    void listFor_withUserName_returnsOrdersByEmail() {
        Authentication auth = mock(Authentication.class);

        doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .when(auth).getAuthorities();

        when(auth.getName()).thenReturn("user@example.com");

        when(orderRepo.findAllByUserEmailOrderByIdDesc("user@example.com"))
                .thenReturn(List.of(orderWithItem(1L, user(1L, "user@example.com"))));

        var result = orderService.listFor(auth);

        assertThat(result).hasSize(1);
        verify(orderRepo).findAllByUserEmailOrderByIdDesc("user@example.com");
    }

    @Test
    void listFor_withoutName_resolvesUserDetailsPrincipal() {
        Authentication auth = mock(Authentication.class);

        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername("principal@example.com")
                .password("hash")
                .authorities("ROLE_USER")
                .build();

        doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .when(auth).getAuthorities();

        when(auth.getName()).thenReturn("");
        when(auth.getPrincipal()).thenReturn(principal);
        when(userRepo.findByEmail("principal@example.com"))
                .thenReturn(Optional.of(user(5L, "principal@example.com")));

        when(orderRepo.findByUserIdOrderByCreatedAtDesc(5L))
                .thenReturn(List.of(orderWithItem(2L, user(5L, "principal@example.com"))));

        var result = orderService.listFor(auth);

        assertThat(result).hasSize(1);
        verify(orderRepo).findByUserIdOrderByCreatedAtDesc(5L);
    }

    @Test
    void listFor_withoutAuthentication_throwsIllegalState() {
        assertThatThrownBy(() -> orderService.listFor(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Niet ingelogd");
    }

    @Test
    void listFor_withUnknownPrincipal_throwsIllegalState() {
        Authentication auth = mock(Authentication.class);

        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername("missing@example.com")
                .password("hash")
                .authorities("ROLE_USER")
                .build();

        doReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")))
                .when(auth).getAuthorities();

        when(auth.getName()).thenReturn("");
        when(auth.getPrincipal()).thenReturn(principal);
        when(userRepo.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.listFor(auth))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Gebruiker niet gevonden");
    }

    @Test
    void getDtoById_existingOrder_returnsDto() {
        Order order = orderWithItem(1L, user(1L, "user@example.com"));

        when(orderRepo.findById(1L)).thenReturn(Optional.of(order));

        var dto = orderService.getDtoById(1L);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getCustomerEmail()).isEqualTo("user@example.com");
        assertThat(dto.getItems()).hasSize(1);
    }

    @Test
    void findByIdOrThrow_missingOrder_throwsIllegalArgument() {
        when(orderRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.findByIdOrThrow(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Order niet gevonden");
    }

    @Test
    void markPaid_setsPaid_andPersists() {
        when(orderRepo.save(pending)).thenReturn(pending);

        Order res = orderService.markPaid(pending);

        assertThat(res.getStatus()).isEqualTo(OrderStatus.PAID);
        verify(orderRepo).save(pending);
    }

    @Test
    void markCancelled_setsCancelled_andPersists() {
        when(orderRepo.save(pending)).thenReturn(pending);

        Order res = orderService.markCancelled(pending);

        assertThat(res.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        verify(orderRepo).save(pending);
    }

    @Test
    void cancel_afterPaid_shouldThrow() {
        Order paid = Order.builder()
                .id(2L)
                .status(OrderStatus.PAID)
                .build();

        assertThatThrownBy(() -> orderService.markCancelled(paid))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("kan niet geannuleerd");

        verify(orderRepo, never()).save(any());
    }

    @Test
    void pay_afterCancelled_shouldThrow() {
        Order cancelled = Order.builder()
                .id(3L)
                .status(OrderStatus.CANCELLED)
                .build();

        assertThatThrownBy(() -> orderService.markPaid(cancelled))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("kan niet betaald");

        verify(orderRepo, never()).save(any());
    }

    @Test
    void toDto_withoutUser_mapsOrderWithoutCustomerFields() {
        Order order = Order.builder()
                .id(1L)
                .createdAt(Instant.parse("2025-01-01T00:00:00Z"))
                .status(OrderStatus.PENDING)
                .total(BigDecimal.ZERO)
                .build();

        var dto = orderService.toDto(order);

        assertThat(dto.getCustomerName()).isNull();
        assertThat(dto.getCustomerEmail()).isNull();
        assertThat(dto.getItems()).isEmpty();
    }

    private CheckoutRequestDTO checkoutRequest(String type, OrderItemDTO item) {
        CheckoutRequestDTO request = new CheckoutRequestDTO();
        request.setType(type);
        request.setItems(List.of(item));
        return request;
    }

    private OrderItemDTO item(Long productId, String type, int quantity, RentalDTO rental) {
        OrderItemDTO item = new OrderItemDTO();
        item.setProductId(productId);
        item.setType(type);
        item.setQuantity(quantity);
        item.setRental(rental);
        return item;
    }

    private User user(Long id, String email) {
        return User.builder()
                .id(id)
                .email(email)
                .name("User")
                .build();
    }

    private Product product(Long id, String title, String price, String rentalPrice) {
        return Product.builder()
                .id(id)
                .title(title)
                .price(new BigDecimal(price))
                .rentalPrice(new BigDecimal(rentalPrice))
                .build();
    }

    private Order orderWithItem(Long id, User user) {
        Product product = product(10L, "Book", "15.00", "3.00");

        Order order = Order.builder()
                .id(id)
                .user(user)
                .createdAt(Instant.parse("2025-01-01T00:00:00Z"))
                .status(OrderStatus.PENDING)
                .total(new BigDecimal("15.00"))
                .build();

        OrderItem item = OrderItem.builder()
                .id(20L)
                .order(order)
                .product(product)
                .type("BUY")
                .quantity(1)
                .priceAtPurchase(new BigDecimal("15.00"))
                .build();

        order.getItems().add(item);

        return order;
    }
}