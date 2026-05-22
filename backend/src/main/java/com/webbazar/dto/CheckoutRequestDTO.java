package com.webbazar.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public class CheckoutRequestDTO {

    @NotNull(message = "type is verplicht")
    @Pattern(regexp = "^(BUY|RENT)$", message = "type moet BUY of RENT zijn")
    private String type;

    @NotEmpty(message = "items mag niet leeg zijn")
    @Valid
    private List<OrderItemDTO> items;

    public CheckoutRequestDTO() {}

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public List<OrderItemDTO> getItems() { return items; }
    public void setItems(List<OrderItemDTO> items) { this.items = items; }
}
