package com.webbazar.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;


 // DTO voor items in een order (request + response).

public class OrderItemDTO {

    private Long id;

    @NotNull(message = "productId is verplicht")
    private Long productId;

    private String productTitle;

    @Pattern(regexp = "^(BUY|RENT)$", message = "type moet BUY of RENT zijn")
    private String type;

    private BigDecimal priceAtPurchase;

    @Min(value = 1, message = "quantity moet >= 1 zijn")
    private int quantity = 1;

    // Indien huur: gevuld, anders null
    private RentalDTO rental;

    public OrderItemDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductTitle() { return productTitle; }
    public void setProductTitle(String productTitle) { this.productTitle = productTitle; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public BigDecimal getPriceAtPurchase() { return priceAtPurchase; }
    public void setPriceAtPurchase(BigDecimal priceAtPurchase) { this.priceAtPurchase = priceAtPurchase; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public RentalDTO getRental() { return rental; }
    public void setRental(RentalDTO rental) { this.rental = rental; }
}
