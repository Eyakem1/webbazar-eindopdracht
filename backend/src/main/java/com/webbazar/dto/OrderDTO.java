package com.webbazar.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private Instant createdAt;
    private String status;
    private BigDecimal total;
    private List<OrderItemDTO> items;

    //  admin-overzicht
    private String customerName;
    private String customerEmail;
}
