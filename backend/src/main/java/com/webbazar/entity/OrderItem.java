package com.webbazar.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@Entity
@Table(name = "order_item")
public class OrderItem {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne(optional = false)
    private Product product;

    // "BUY" of "RENT" Type bestelling
    @Column(nullable = false, length = 10)
    private String type;

    @Min(1)
    @Column(nullable = false)
    @Builder.Default
    private int quantity = 1;

    @Column(name = "price_at_purchase", precision = 12, scale = 2, nullable = false)
    private BigDecimal priceAtPurchase;

    @OneToOne(mappedBy = "orderItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private Rental rental;

    public void setRental(Rental r) {
        this.rental = r;
        if (r != null) r.setOrderItem(this);
    }

    // Koppeling met order
    public void attachTo(Order o) {
        this.order = o;
        if (o != null && !o.getItems().contains(this)) {
            o.getItems().add(this);
        }
    }
}
