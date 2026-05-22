package com.webbazar.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class ProductDTO {

    private Long id;

    @NotBlank(message = "Titel is verplicht")
    @Size(max = 150, message = "Titel mag maximaal 150 tekens zijn")
    private String title;

    @Size(max = 150, message = "Auteur mag maximaal 150 tekens zijn")
    private String author;

    @Size(max = 500, message = "Beschrijving mag maximaal 500 tekens zijn")
    private String description;

    @NotNull(message = "Prijs is verplicht")
    @DecimalMin(value = "0.0", inclusive = false, message = "Prijs moet groter zijn dan 0")
    private BigDecimal price;

    @NotNull(message = "Huurprijs is verplicht")
    @DecimalMin(value = "0.0", inclusive = true, message = "Huurprijs kan niet negatief zijn")
    private BigDecimal rentalPrice;

    @Size(max = 255, message = "Bestandspad is te lang")
    private String filePath;

    // Constructors
    public ProductDTO() {
    }

    //  Extra constructor voor  tests
    public ProductDTO(Long id, String title, String description, BigDecimal price) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.price = price;
    }

    // all-args constructor
    public ProductDTO(Long id, String title, String author, String description,
                      BigDecimal price, BigDecimal rentalPrice, String filePath) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.description = description;
        this.price = price;
        this.rentalPrice = rentalPrice;
        this.filePath = filePath;
    }

    //  Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public BigDecimal getRentalPrice() { return rentalPrice; }
    public void setRentalPrice(BigDecimal rentalPrice) { this.rentalPrice = rentalPrice; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }
}
