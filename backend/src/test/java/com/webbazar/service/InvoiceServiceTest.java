package com.webbazar.service;

import com.webbazar.entity.Order;
import com.webbazar.entity.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.Instant;

import static org.assertj.core.api.Assertions.*;

class InvoiceServiceTest {

    private final InvoiceService invoiceService = new InvoiceService();

    @Test
    void renderInvoice_withCompleteOrder_returnsPdfBytes() {
        User user = User.builder()
                .name("Robel")
                .email("robel@example.com")
                .build();

        Order order = Order.builder()
                .id(1L)
                .createdAt(Instant.parse("2025-01-01T10:00:00Z"))
                .total(new BigDecimal("19.99"))
                .user(user)
                .build();

        byte[] pdf = invoiceService.renderInvoice(order);

        assertThat(pdf).isNotEmpty();
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    void renderInvoice_withNullFields_usesFallbackValues() {
        Order order = new Order();

        byte[] pdf = invoiceService.renderInvoice(order);

        assertThat(pdf).isNotEmpty();
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    void renderInvoice_withSpecialCharacters_stillReturnsPdfBytes() {
        User user = User.builder()
                .name("Robël €")
                .email(null)
                .build();

        Order order = Order.builder()
                .id(2L)
                .createdAt(Instant.parse("2025-01-01T10:00:00Z"))
                .total(new BigDecimal("10.00"))
                .user(user)
                .build();

        byte[] pdf = invoiceService.renderInvoice(order);

        assertThat(pdf).isNotEmpty();
        assertThat(new String(pdf, 0, 4)).isEqualTo("%PDF");
    }

    @Test
    void renderInvoice_withNullOrder_wrapsException() {
        assertThatThrownBy(() -> invoiceService.renderInvoice(null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Kon factuur niet genereren");
    }
}