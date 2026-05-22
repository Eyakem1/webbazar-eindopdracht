package com.webbazar.service.impl;

import com.webbazar.dto.CheckoutRequestDTO;
import com.webbazar.dto.OrderDTO;
import com.webbazar.dto.OrderItemDTO;
import com.webbazar.dto.RentalDTO;
import com.webbazar.entity.*;
import com.webbazar.repo.OrderRepository;
import com.webbazar.repo.ProductRepository;
import com.webbazar.repo.UserRepository;
import com.webbazar.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;


      // Helpers

    private boolean isAdmin(Authentication auth) {
        if (auth == null) return false;
        for (GrantedAuthority ga : auth.getAuthorities()) {
            if ("ROLE_ADMIN".equals(ga.getAuthority())) return true;
        }
        return false;
    }

    private User resolveCurrentUser(Authentication auth) {
        if (auth == null || !(auth.getPrincipal() instanceof UserDetails ud)) {
            throw new IllegalStateException("Niet ingelogd");
        }
        String email = ud.getUsername();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Gebruiker niet gevonden"));
    }

    private User resolveUserByEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email ontbreekt");
        }
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Gebruiker niet gevonden voor email: " + email));
    }


      // Service methods

    @Override
    public OrderDTO checkout(CheckoutRequestDTO req, String email) {
        User user = resolveUserByEmail(email);

        Order order = Order.builder()
                .user(user)
                .status(OrderStatus.PENDING)
                .createdAt(Instant.now())
                .build();

        BigDecimal total = BigDecimal.ZERO;

        // Fallback naar request type
        for (OrderItemDTO in : req.getItems()) {
            Product product = productRepository.findById(in.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Product niet gevonden: " + in.getProductId()));

            String itemType = (in.getType() != null && !in.getType().isBlank())
                    ? in.getType()
                    : req.getType();

            boolean rent = "RENT".equalsIgnoreCase(itemType);
            BigDecimal unit = rent ? product.getRentalPrice() : product.getPrice();
            int qty = Math.max(1, in.getQuantity());

            OrderItem oi = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .type(rent ? "RENT" : "BUY")
                    .quantity(qty)
                    .priceAtPurchase(unit)
                    .build();

            // Koppeling met order
            order.getItems().add(oi);

            // Huurperiode instellen
            RentalDTO reqRental = in.getRental();
            if (rent) {
                Instant start = (reqRental != null && reqRental.getStartDate() != null)
                        ? reqRental.getStartDate()
                        : Instant.now();
                Instant end = (reqRental != null && reqRental.getEndDate() != null)
                        ? reqRental.getEndDate()
                        : Instant.now().plusSeconds(7 * 86400);

                Rental r = Rental.builder()
                        .orderItem(oi)
                        .startDate(start)
                        .endDate(end)
                        .build();
                oi.setRental(r);
            }

            total = total.add(unit.multiply(BigDecimal.valueOf(qty)));
        }

        order.setTotal(total);
        Order saved = orderRepository.save(order);
        return toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDTO> listFor(Authentication auth) {
        List<Order> orders;
        if (isAdmin(auth)) {
            // admin ziet alle orders
            orders = orderRepository.findAllByOrderByIdDesc();
        } else {
            // Orders van ingelogde gebruiker
            String email = auth != null ? auth.getName() : null;
            if (email != null && !email.isBlank()) {
                orders = orderRepository.findAllByUserEmailOrderByIdDesc(email);
            } else {
                User u = resolveCurrentUser(auth);
                orders = orderRepository.findByUserIdOrderByCreatedAtDesc(u.getId());
            }
        }
        return orders.stream()
                .map(this::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDTO getDtoById(Long id) {
        return toDto(findByIdOrThrow(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Order findByIdOrThrow(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order niet gevonden: " + orderId));
    }

    @Override
    public Order markPaid(Order order) {
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Geannuleerde order kan niet betaald worden");
        }
        order.setStatus(OrderStatus.PAID);
        return orderRepository.save(order);
    }

    @Override
    public Order markCancelled(Order order) {
        if (order.getStatus() == OrderStatus.PAID) {
            throw new IllegalStateException("Betaalde order kan niet geannuleerd worden");
        }
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    @Override
    public OrderDTO toDto(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setStatus(order.getStatus().name());
        dto.setTotal(order.getTotal());

        //  klantgegevens toevoegen
        if (order.getUser() != null) {
            dto.setCustomerName(order.getUser().getName());
            dto.setCustomerEmail(order.getUser().getEmail());
        }

        List<OrderItemDTO> items = order.getItems().stream().map(it -> {
            OrderItemDTO d = new OrderItemDTO();
            d.setId(it.getId());
            d.setProductId(it.getProduct().getId());
            d.setProductTitle(it.getProduct().getTitle());
            d.setType(it.getType());
            d.setQuantity(it.getQuantity());
            d.setPriceAtPurchase(it.getPriceAtPurchase());
            if (it.getRental() != null) {
                RentalDTO r = new RentalDTO();
                r.setStartDate(it.getRental().getStartDate());
                r.setEndDate(it.getRental().getEndDate());
                d.setRental(r);
            }
            return d;
        }).toList();

        dto.setItems(items);
        return dto;
    }
}
